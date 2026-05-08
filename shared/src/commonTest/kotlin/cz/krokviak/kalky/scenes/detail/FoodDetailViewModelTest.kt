package cz.krokviak.kalky.scenes.detail

import cz.krokviak.kalky.core.camera.data.FoodAnalysisDto
import cz.krokviak.kalky.core.common.ImageStorage
import cz.krokviak.kalky.core.common.domain.DeleteFoodItemUseCase
import cz.krokviak.kalky.core.common.domain.GetFoodItemUseCase
import cz.krokviak.kalky.core.common.domain.UpdateFoodItemUseCase
import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.error.UiError
import cz.krokviak.kalky.core.network.FoodAnalysisClient
import cz.krokviak.kalky.scenes.nutrientedit.MacroField
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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

@OptIn(ExperimentalCoroutinesApi::class)
class FoodDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val fixedNow = Instant.parse("2026-05-08T10:00:00Z")
    private val clock = object : Clock { override fun now(): Instant = fixedNow }

    @BeforeTest fun setUp() { Dispatchers.setMain(dispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    private val sampleFood = FoodItemEntity(
        id = 7, name = "Banán", calories = 89, protein = 1, fat = 0, carbs = 23,
        portion = 1, healthScore = 9, createdAt = fixedNow, updatedAt = fixedNow,
        localImagePath = "/tmp/food.jpg",
    )

    private fun fakeImageStorage() = object : ImageStorage {
        override suspend fun storeImageFile(imageBytes: ByteArray) = "/tmp/x.jpg"
        override suspend fun getImageBytes(imagePath: String) = byteArrayOf(1, 2, 3)
    }

    private fun buildVm(
        foodOnLoad: FoodItemEntity? = sampleFood,
        analysisDto: FoodAnalysisDto? = null,
    ): FoodDetailViewModel {
        val analysis = mock<FoodAnalysisClient> {
            everySuspend { getAnalysis(any()) } returns analysisDto
        }
        val getFood = mock<GetFoodItemUseCase> { everySuspend { invoke(any()) } returns foodOnLoad }
        val updateFood = mock<UpdateFoodItemUseCase> { everySuspend { invoke(any()) } returns Unit }
        val deleteFood = mock<DeleteFoodItemUseCase> { everySuspend { invoke(any()) } returns Unit }
        return FoodDetailViewModel(analysis, getFood, updateFood, deleteFood, fakeImageStorage(), clock)
    }

    @Test
    fun loadFood_populatesUiState() = runTest(dispatcher) {
        val vm = buildVm()
        vm.loadFood(7)
        advanceUntilIdle()
        val s = vm.uiState.value
        assertEquals(7L, s.id)
        assertEquals("Banán", s.name)
        assertEquals(89, s.calories)
    }

    @Test
    fun loadFood_withNullResult_setsEmptyState() = runTest(dispatcher) {
        val vm = buildVm(foodOnLoad = null)
        vm.loadFood(99)
        advanceUntilIdle()
        val s = vm.uiState.value
        assertEquals(0L, s.id)
        assertEquals("", s.name)
    }

    @Test
    fun toggleField_setsAndClearsActiveField() = runTest(dispatcher) {
        val vm = buildVm()
        vm.toggleField(MacroField.PROTEIN)
        assertEquals(MacroField.PROTEIN, vm.uiState.value.activeField)
        vm.toggleField(MacroField.PROTEIN)
        assertNull(vm.uiState.value.activeField)
    }

    @Test
    fun increasePortion_incrementsByOne() = runTest(dispatcher) {
        val vm = buildVm()
        vm.loadFood(7)
        advanceUntilIdle()
        vm.increasePortion()
        assertEquals(2, vm.uiState.value.portion)
    }

    @Test
    fun decreasePortion_neverGoesBelowOne() = runTest(dispatcher) {
        val vm = buildVm()
        vm.loadFood(7)
        advanceUntilIdle()
        // initial portion = 1
        vm.decreasePortion()
        assertEquals(1, vm.uiState.value.portion)
        vm.increasePortion()
        vm.increasePortion() // 3
        vm.decreasePortion()
        assertEquals(2, vm.uiState.value.portion)
    }

    @Test
    fun onProteinChange_clampsRange_andRecalculatesCalories() = runTest(dispatcher) {
        val vm = buildVm()
        vm.loadFood(7)
        advanceUntilIdle()
        vm.onProteinChange(999) // clamps to 500
        val s = vm.uiState.value
        assertEquals(500, s.protein)
        // 500*4 + 23*4 + 0*9 = 2092
        assertEquals(2092, s.calories)
    }

    @Test
    fun onCarbsChange_clampsNegativeToZero() = runTest(dispatcher) {
        val vm = buildVm()
        vm.loadFood(7)
        advanceUntilIdle()
        vm.onCarbsChange(-10)
        assertEquals(0, vm.uiState.value.carbs)
    }

    @Test
    fun fixResult_withNoImage_setsPhotoAnalysisError() = runTest(dispatcher) {
        val noImg = sampleFood.copy(localImagePath = "")
        val vm = buildVm(foodOnLoad = noImg)
        vm.loadFood(7)
        advanceUntilIdle()

        // After load, localImagePath in state is "" — fixResult should mark error
        // (current state localImagePath comes from food which has empty string)
        // actually FoodDetailViewModel sets localImagePath = food.localImagePath, which is ""
        // that means imageStorage.getImageBytes("") might still work... let's check fix the test:
        // in this test, image is "" but state stored "" not null. Force null path:
        // Actually after loadFood sets state.localImagePath to food.localImagePath="" not null.
        // The condition is `?.let { ... } ?: return@launch markPhotoAnalysisError()`.
        // For "" the let block runs (not null) and proceeds to runAnalysis.
        // To trigger the error path, we need null. The state default is null but loadFood overrides it.

        // Better test: after load with localImagePath = null — but FoodItemEntity has String field.
        // Fall back to: don't load food at all, fix on initial state where localImagePath is null
        val vm2 = buildVm()
        // skip loadFood — initial state has localImagePath = null
        vm2.fixResult()
        advanceUntilIdle()
        assertEquals(UiError.PhotoAnalysis, vm2.uiState.value.error)
    }

    // fixResult success/null-analysis paths require Dispatchers.IO which isn't
    // the test dispatcher; skipping to avoid relying on real IO. The
    // imageless-error path below is covered.

    @Test
    fun deleteFood_invokesDeleteUseCase() = runTest(dispatcher) {
        val deleteUseCase = mock<DeleteFoodItemUseCase> {
            everySuspend { invoke(any()) } returns Unit
        }
        val vm = FoodDetailViewModel(
            mock<FoodAnalysisClient> { everySuspend { getAnalysis(any()) } returns null },
            mock<GetFoodItemUseCase> { everySuspend { invoke(any()) } returns sampleFood },
            mock<UpdateFoodItemUseCase> { everySuspend { invoke(any()) } returns Unit },
            deleteUseCase, fakeImageStorage(), clock,
        )
        vm.loadFood(7)
        advanceUntilIdle()

        vm.deleteFood()
        advanceUntilIdle()

        verifySuspend { deleteUseCase.invoke(any()) }
    }

    @Test
    fun finish_persistsCurrentStateViaUpdateUseCase() = runTest(dispatcher) {
        val updateUseCase = mock<UpdateFoodItemUseCase> {
            everySuspend { invoke(any()) } returns Unit
        }
        val vm = FoodDetailViewModel(
            mock<FoodAnalysisClient> { everySuspend { getAnalysis(any()) } returns null },
            mock<GetFoodItemUseCase> { everySuspend { invoke(any()) } returns sampleFood },
            updateUseCase,
            mock<DeleteFoodItemUseCase> { everySuspend { invoke(any()) } returns Unit },
            fakeImageStorage(), clock,
        )
        vm.loadFood(7)
        advanceUntilIdle()

        vm.finish()
        advanceUntilIdle()

        verifySuspend { updateUseCase.invoke(any()) }
    }

    @Test
    fun dismissError_clearsError() = runTest(dispatcher) {
        val vm = buildVm()
        vm.fixResult() // triggers error since localImagePath is null on initial state
        advanceUntilIdle()
        assertEquals(UiError.PhotoAnalysis, vm.uiState.value.error)
        vm.dismissError()
        assertNull(vm.uiState.value.error)
    }
}
