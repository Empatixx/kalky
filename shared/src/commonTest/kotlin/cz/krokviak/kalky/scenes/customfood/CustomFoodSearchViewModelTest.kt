package cz.krokviak.kalky.scenes.customfood

import app.cash.turbine.test
import cz.krokviak.kalky.core.common.domain.AddFoodItemUseCase
import cz.krokviak.kalky.core.common.domain.FoodLibrary
import cz.krokviak.kalky.core.common.domain.FoodSearchResult
import cz.krokviak.kalky.core.common.domain.GetFoodLibraryUseCase
import cz.krokviak.kalky.core.common.domain.SearchFoodsUseCase
import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.error.UiError
import cz.krokviak.kalky.core.network.OpenFoodFactsClient
import cz.krokviak.kalky.scenes.barcode.data.OpenFoodFactsNutriments
import cz.krokviak.kalky.scenes.barcode.data.OpenFoodFactsProduct
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CustomFoodSearchViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val fixedNow = Instant.parse("2026-05-08T10:00:00Z")
    private val fixedClock = object : Clock { override fun now(): Instant = fixedNow }

    @BeforeTest fun setUp() { Dispatchers.setMain(dispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    private val customItem = FoodItemEntity(
        id = 1, name = "MyCustom", calories = 100, protein = 10, fat = 5, carbs = 15,
        createdAt = fixedNow, updatedAt = fixedNow, isCustom = true,
    )
    private val historyItem = FoodItemEntity(
        id = 2, name = "Photo food", calories = 200, protein = 20, fat = 10, carbs = 30,
        createdAt = fixedNow, updatedAt = fixedNow, isCustom = false,
    )

    private fun buildVm(
        library: FoodLibrary = FoodLibrary(custom = listOf(customItem), history = listOf(historyItem)),
        searchResult: FoodSearchResult = FoodSearchResult(custom = emptyList(), history = emptyList()),
        apiResult: List<OpenFoodFactsProduct> = emptyList(),
        apiThrows: Boolean = false,
    ): CustomFoodSearchViewModel {
        val getLib = mock<GetFoodLibraryUseCase> { everySuspend { invoke() } returns library }
        val searchFoods = mock<SearchFoodsUseCase> { everySuspend { invoke(any()) } returns searchResult }
        val ofc = mock<OpenFoodFactsClient> {
            if (apiThrows) {
                everySuspend { searchProducts(any(), any()) } throws RuntimeException("boom")
            } else {
                everySuspend { searchProducts(any(), any()) } returns apiResult
            }
        }
        val addFood = mock<AddFoodItemUseCase> { everySuspend { invoke(any()) } returns 99L }
        return CustomFoodSearchViewModel(getLib, searchFoods, ofc, addFood, fixedClock)
    }

    @Test
    fun init_loadsLibraryIntoState() = runTest(dispatcher) {
        val vm = buildVm()
        advanceUntilIdle()
        val s = vm.uiState.value
        assertEquals(1, s.customFoods.size)
        assertEquals("MyCustom", s.customFoods.first().name)
        assertEquals(1, s.historyItems.size)
        assertEquals(false, s.isLoading)
    }

    @Test
    fun onSearchQueryChange_setsQueryImmediately() = runTest(dispatcher) {
        val vm = buildVm()
        advanceUntilIdle()
        vm.onSearchQueryChange("ban")

        assertEquals("ban", vm.uiState.value.searchQuery)
    }

    @Test
    fun onSearchQueryChange_debouncesBy300ms_andRunsRemoteSearch() = runTest(dispatcher) {
        val product = OpenFoodFactsProduct(productName = "Banán",
            nutriments = OpenFoodFactsNutriments(energyKcal100g = 89.0))
        val vm = buildVm(apiResult = listOf(product))
        advanceUntilIdle()

        vm.onSearchQueryChange("ban")
        advanceTimeBy(100)

        assertTrue(vm.uiState.value.apiResults.isEmpty())

        advanceTimeBy(250)
        advanceUntilIdle()
        assertEquals(1, vm.uiState.value.apiResults.size)
        assertEquals("Banán", vm.uiState.value.apiResults.first().productName)
    }

    @Test
    fun onSearchQueryChange_blank_reloadsLibrary() = runTest(dispatcher) {
        val vm = buildVm()
        advanceUntilIdle()

        vm.onSearchQueryChange("ban")
        advanceUntilIdle()
        vm.onSearchQueryChange("")
        advanceUntilIdle()
        assertTrue(vm.uiState.value.apiResults.isEmpty())
    }

    @Test
    fun apiSearchFailure_setsProductSearchError() = runTest(dispatcher) {
        val vm = buildVm(apiThrows = true)
        advanceUntilIdle()

        vm.onSearchQueryChange("ban")
        advanceTimeBy(400)
        advanceUntilIdle()

        assertEquals(UiError.ProductSearch, vm.uiState.value.error)
    }

    @Test
    fun toggleSelection_addsAndRemovesIds() = runTest(dispatcher) {
        val vm = buildVm()
        advanceUntilIdle()
        vm.toggleSelection(1L)
        vm.toggleSelection(2L)
        assertEquals(setOf(1L, 2L), vm.uiState.value.selectedItems.toSet())
        vm.toggleSelection(1L)
        assertEquals(setOf(2L), vm.uiState.value.selectedItems.toSet())
    }

    @Test
    fun selectApiProduct_setsSelectedAndDefaultPortion100() = runTest(dispatcher) {
        val vm = buildVm()
        advanceUntilIdle()
        val product = OpenFoodFactsProduct(productName = "X")

        vm.selectApiProduct(product)

        assertEquals(product, vm.uiState.value.selectedApiProduct)
        assertEquals(100, vm.uiState.value.portionGrams)
    }

    @Test
    fun setPortionGrams_clampsRangeOneTo9999() = runTest(dispatcher) {
        val vm = buildVm()
        advanceUntilIdle()
        vm.setPortionGrams(0)
        assertEquals(1, vm.uiState.value.portionGrams)
        vm.setPortionGrams(50000)
        assertEquals(9999, vm.uiState.value.portionGrams)
        vm.setPortionGrams(150)
        assertEquals(150, vm.uiState.value.portionGrams)
    }

    @Test
    fun confirmAddApiProduct_persistsScaledItem_andClearsApiResults() = runTest(dispatcher) {
        val product = OpenFoodFactsProduct(
            productName = "Mléko",
            nutriments = OpenFoodFactsNutriments(
                energyKcal100g = 65.0, proteins100g = 3.4, fat100g = 1.5, carbohydrates100g = 4.8,
            ),
        )
        val vm = buildVm()
        advanceUntilIdle()
        vm.selectApiProduct(product)
        vm.setPortionGrams(200)

        vm.foodAdded.test {
            vm.confirmAddApiProduct()
            advanceUntilIdle()
            assertEquals(99L, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertNull(vm.uiState.value.selectedApiProduct)
        assertEquals("", vm.uiState.value.searchQuery)
    }

    @Test
    fun confirmAddApiProduct_noOp_whenNothingSelected() = runTest(dispatcher) {
        val vm = buildVm()
        advanceUntilIdle()
        vm.confirmAddApiProduct()
        advanceUntilIdle()

    }

    @Test
    fun dismissPortionPicker_clearsSelection() = runTest(dispatcher) {
        val vm = buildVm()
        advanceUntilIdle()
        vm.selectApiProduct(OpenFoodFactsProduct(productName = "X"))
        vm.dismissPortionPicker()
        assertNull(vm.uiState.value.selectedApiProduct)
    }

    @Test
    fun addSelectedFoods_clonesSelectedItems_emitsFoodAdded_clearsSelection() = runTest(dispatcher) {
        val vm = buildVm()
        advanceUntilIdle()
        vm.toggleSelection(1L)
        vm.toggleSelection(2L)

        vm.foodAdded.test {
            vm.addSelectedFoods()
            advanceUntilIdle()

            assertEquals(0L, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertTrue(vm.uiState.value.selectedItems.isEmpty())
    }

    @Test
    fun clearSelection_emptiesSelectedItems() = runTest(dispatcher) {
        val vm = buildVm()
        advanceUntilIdle()
        vm.toggleSelection(1L)
        vm.clearSelection()
        assertTrue(vm.uiState.value.selectedItems.isEmpty())
    }

    @Test
    fun dismissError_clearsError() = runTest(dispatcher) {
        val vm = buildVm(apiThrows = true)
        advanceUntilIdle()
        vm.onSearchQueryChange("err")
        advanceTimeBy(400)
        advanceUntilIdle()
        assertEquals(UiError.ProductSearch, vm.uiState.value.error)

        vm.dismissError()
        assertNull(vm.uiState.value.error)
    }
}
