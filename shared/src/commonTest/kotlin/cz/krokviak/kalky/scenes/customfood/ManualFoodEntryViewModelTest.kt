package cz.krokviak.kalky.scenes.customfood

import app.cash.turbine.test
import cz.krokviak.kalky.core.common.domain.AddFoodItemUseCase
import cz.krokviak.kalky.core.common.domain.SearchHistoryFoodsUseCase
import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.network.OpenFoodFactsClient
import cz.krokviak.kalky.scenes.barcode.data.OpenFoodFactsNutriments
import cz.krokviak.kalky.scenes.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalky.scenes.nutrientedit.MacroField
import dev.mokkery.answering.returns
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
class ManualFoodEntryViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val fixedNow = Instant.parse("2026-05-08T10:00:00Z")
    private val fixedClock = object : Clock { override fun now(): Instant = fixedNow }

    @BeforeTest fun setUp() { Dispatchers.setMain(dispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    private fun buildVm(): ManualFoodEntryViewModel {
        val searchHistory = mock<SearchHistoryFoodsUseCase> {
            everySuspend { invoke(any()) } returns emptyList()
        }
        val ofc = mock<OpenFoodFactsClient>()
        val addFood = mock<AddFoodItemUseCase> {
            everySuspend { invoke(any()) } returns 1L
        }
        return ManualFoodEntryViewModel(searchHistory, ofc, addFood, fixedClock)
    }

    @Test
    fun onProteinChange_updatesCalories_via4kcalPerGram() {
        val vm = buildVm()
        vm.onProteinChange(50)

        assertEquals(50, vm.state.value.protein)
        assertEquals(200, vm.state.value.calories)
    }

    @Test
    fun onCarbsAndFatChange_aggregateCalories() {
        val vm = buildVm()
        vm.onProteinChange(20)
        vm.onCarbsChange(50)
        vm.onFatChange(10)

        assertEquals(370, vm.state.value.calories)
    }

    @Test
    fun toggleField_setsThenClearsActiveField() {
        val vm = buildVm()
        vm.toggleField(MacroField.PROTEIN)
        assertEquals(MacroField.PROTEIN, vm.state.value.activeField)
        vm.toggleField(MacroField.PROTEIN)
        assertNull(vm.state.value.activeField)
    }

    @Test
    fun toggleField_switchesBetweenFields() {
        val vm = buildVm()
        vm.toggleField(MacroField.PROTEIN)
        vm.toggleField(MacroField.FAT)
        assertEquals(MacroField.FAT, vm.state.value.activeField)
    }

    @Test
    fun onNameChange_updatesName() {
        val vm = buildVm()
        vm.onNameChange("Tortilla")
        assertEquals("Tortilla", vm.state.value.name)
    }

    @Test
    fun reset_clearsAllState() {
        val vm = buildVm()
        vm.onNameChange("Foo")
        vm.onProteinChange(99)
        vm.toggleField(MacroField.CARBS)
        vm.reset()
        val s = vm.state.value
        assertEquals("", s.name)
        assertEquals(0, s.protein)
        assertEquals(0, s.calories)
        assertNull(s.activeField)
    }

    @Test
    fun submit_emitsNewIdAndPersistsCustomItem() = runTest(dispatcher) {
        val vm = buildVm()
        vm.onNameChange("Custom Snack")
        vm.onProteinChange(10)
        vm.foodAdded.test {
            vm.submit()
            advanceUntilIdle()
            assertEquals(1L, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun setSourceFoods_recalculatesTotalsFromPortions100g() = runTest(dispatcher) {
        val vm = buildVm()
        val foods = listOf(
            FoodItemEntity(id = 1, name = "A", calories = 0, protein = 10, carbs = 20, fat = 5,
                createdAt = fixedNow, updatedAt = fixedNow),
            FoodItemEntity(id = 2, name = "B", calories = 0, protein = 5, carbs = 30, fat = 2,
                createdAt = fixedNow, updatedAt = fixedNow),
        )
        vm.setSourceFoods(foods)
        val s = vm.state.value
        assertEquals(15, s.protein)
        assertEquals(50, s.carbs)
        assertEquals(7, s.fat)
    }

    @Test
    fun updateSourcePortion_scalesMacrosProportionally() = runTest(dispatcher) {
        val vm = buildVm()
        vm.setSourceFoods(listOf(
            FoodItemEntity(id = 1, name = "A", protein = 10, carbs = 20, fat = 5,
                calories = 0, createdAt = fixedNow, updatedAt = fixedNow),
        ))

        vm.updateSourcePortion(1, 200)

        assertEquals(20, vm.state.value.protein)
        assertEquals(40, vm.state.value.carbs)
    }

    @Test
    fun removeSourceFood_dropsItemAndRecalculates() = runTest(dispatcher) {
        val vm = buildVm()
        vm.setSourceFoods(listOf(
            FoodItemEntity(id = 1, name = "A", protein = 10, carbs = 20, fat = 5,
                calories = 0, createdAt = fixedNow, updatedAt = fixedNow),
            FoodItemEntity(id = 2, name = "B", protein = 5, carbs = 5, fat = 5,
                calories = 0, createdAt = fixedNow, updatedAt = fixedNow),
        ))
        vm.removeSourceFood(1)
        val s = vm.state.value
        assertEquals(1, s.sourceFoods.size)
        assertEquals(5, s.protein)
        assertTrue(s.sourceFoods.none { it.id == 1L })
    }

    @Test
    fun addSourceFoodFromApi_scalesPer100gIntoEntity() = runTest(dispatcher) {
        val vm = buildVm()
        val product = OpenFoodFactsProduct(
            productName = "Mléko",
            nutriments = OpenFoodFactsNutriments(
                energyKcal100g = 65.0,
                proteins100g = 3.4,
                fat100g = 1.5,
                carbohydrates100g = 4.8,
            )
        )
        vm.addSourceFoodFromApi(product)
        val s = vm.state.value
        assertEquals(1, s.sourceFoods.size)
        assertEquals(3, s.protein)
    }

    @Test
    fun searchIngredients_blankQuery_clearsResults() = runTest(dispatcher) {
        val vm = buildVm()
        vm.searchIngredients("")
        advanceUntilIdle()
        assertTrue(vm.state.value.ingredientResults.isEmpty())
        assertTrue(vm.state.value.ingredientApiResults.isEmpty())
    }

    @Test
    fun searchIngredients_debouncesBy300ms_andEmitsResults() = runTest(dispatcher) {
        val historyItem = FoodItemEntity(
            id = 99, name = "Banán", protein = 1, carbs = 23, fat = 0, calories = 89,
            createdAt = fixedNow, updatedAt = fixedNow,
        )
        val searchHistory = mock<SearchHistoryFoodsUseCase> {
            everySuspend { invoke(any()) } returns listOf(historyItem)
        }
        val ofc = mock<OpenFoodFactsClient> {
            everySuspend { searchProducts(any(), any()) } returns emptyList()
        }
        val addFood = mock<AddFoodItemUseCase> {
            everySuspend { invoke(any()) } returns 1L
        }
        val vm = ManualFoodEntryViewModel(searchHistory, ofc, addFood, fixedClock)

        vm.searchIngredients("ban")

        advanceTimeBy(100)
        assertTrue(vm.state.value.ingredientResults.isEmpty())

        advanceTimeBy(250)
        advanceUntilIdle()
        assertEquals(1, vm.state.value.ingredientResults.size)
        assertEquals("Banán", vm.state.value.ingredientResults.first().name)
    }
}
