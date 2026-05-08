package cz.krokviak.kalky.scenes.home

import app.cash.turbine.test
import cz.krokviak.kalky.core.common.FoodPhotoAnalyzer
import cz.krokviak.kalky.core.common.domain.AddFoodItemUseCase
import cz.krokviak.kalky.core.common.domain.DailyMacros
import cz.krokviak.kalky.core.common.domain.DeleteFoodItemsUseCase
import cz.krokviak.kalky.core.common.domain.GetLatestNutrientSettingsUseCase
import cz.krokviak.kalky.core.common.domain.GetStreakUseCase
import cz.krokviak.kalky.core.common.domain.ObserveDailyMacrosUseCase
import cz.krokviak.kalky.core.common.entities.NutrientSettingEntity
import cz.krokviak.kalky.core.db.DatabaseSeeder
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val fixedNow = Instant.parse("2026-05-08T10:00:00Z")
    private val fixedClock = object : Clock {
        override fun now(): Instant = fixedNow
    }

    @BeforeTest fun setUp() { Dispatchers.setMain(dispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    private fun buildViewModel(
        macrosFlow: kotlinx.coroutines.flow.Flow<DailyMacros> = flowOf(DailyMacros()),
        latestSettings: NutrientSettingEntity? = null,
        streak: Int = 0,
    ): MainViewModel {
        val getLatestSettings = mock<GetLatestNutrientSettingsUseCase> {
            everySuspend { invoke() } returns latestSettings
        }
        val foodPhotoAnalyzer = mock<FoodPhotoAnalyzer>()
        val observeDailyMacros = mock<ObserveDailyMacrosUseCase> {
            every { invoke(any()) } returns macrosFlow
        }
        val getStreak = mock<GetStreakUseCase> {
            everySuspend { invoke() } returns streak
        }
        val addFoodItem = mock<AddFoodItemUseCase>()
        val deleteFoodItems = mock<DeleteFoodItemsUseCase> {
            everySuspend { invoke(any()) } returns Unit
        }
        val databaseSeeder = mock<DatabaseSeeder>()

        return MainViewModel(
            getLatestSettings = getLatestSettings,
            foodPhotoAnalyzer = foodPhotoAnalyzer,
            observeDailyMacros = observeDailyMacros,
            getStreak = getStreak,
            addFoodItem = addFoodItem,
            deleteFoodItems = deleteFoodItems,
            databaseSeeder = databaseSeeder,
            clock = fixedClock,
            seedMockData = false,
        )
    }

    @Test
    fun initialState_hasZeroMacros_andEmptyItems() = runTest(dispatcher) {
        val vm = buildViewModel()

        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(0, state.currentCalories)
        assertEquals(0, state.currentProtein)
        assertEquals(0, state.maxCalories)
        assertTrue(state.recentlyAddedItems.isEmpty())
        assertTrue(state.selectedFoodIds.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun applyLatestNutrientSettings_setsMaxValues_fromLatestSettings() = runTest(dispatcher) {
        val settings = NutrientSettingEntity(
            id = 1,
            targetProtein = 150,
            targetCarbs = 250,
            targetFat = 70,
            targetCalories = 2230,
            createdAt = fixedNow,
            updatedAt = fixedNow,
        )
        val vm = buildViewModel(latestSettings = settings)

        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(150, state.maxProtein)
        assertEquals(250, state.maxCarbs)
        assertEquals(70, state.maxFats)
        assertEquals(2230, state.maxCalories)
    }

    @Test
    fun observeDailyMacros_emission_updatesUiState() = runTest(dispatcher) {
        val macros = DailyMacros(
            items = persistentListOf(),
            totalCalories = 1234,
            totalProtein = 80,
            totalCarbs = 150,
            totalFat = 50,
        )
        val flow = MutableSharedFlow<DailyMacros>(replay = 1)
        flow.tryEmit(macros)
        val vm = buildViewModel(macrosFlow = flow)

        vm.uiState.test {
            // initial state (before flow collection)
            val initial = awaitItem()
            assertEquals(0, initial.currentCalories)

            advanceUntilIdle()

            // after flow emission
            val updated = expectMostRecentItem()
            assertEquals(1234, updated.currentCalories)
            assertEquals(80, updated.currentProtein)
            assertEquals(150, updated.currentCarbs)
            assertEquals(50, updated.currentFats)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun toggleFoodSelection_addsAndRemovesIds() = runTest(dispatcher) {
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.toggleFoodSelection(1L)
        vm.toggleFoodSelection(2L)
        assertEquals(setOf(1L, 2L), vm.uiState.value.selectedFoodIds.toSet())
        assertTrue(vm.uiState.value.isSelectionMode)

        vm.toggleFoodSelection(1L) // remove
        assertEquals(setOf(2L), vm.uiState.value.selectedFoodIds.toSet())
    }

    @Test
    fun clearSelection_emptiesSelectedFoodIds() = runTest(dispatcher) {
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.toggleFoodSelection(1L)
        vm.toggleFoodSelection(2L)
        vm.clearSelection()

        assertTrue(vm.uiState.value.selectedFoodIds.isEmpty())
        assertTrue(!vm.uiState.value.isSelectionMode)
    }

    @Test
    fun onDateSelected_updatesCurrentDate() = runTest(dispatcher) {
        val vm = buildViewModel()
        advanceUntilIdle()

        val newDate = LocalDate(2026, 1, 15)
        vm.onDateSelected(newDate)

        assertEquals(newDate, vm.uiState.value.currentDate)
    }

    @Test
    fun updateNutrientSettings_overridesMaxValues() = runTest(dispatcher) {
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.updateNutrientSettings(protein = 100, carbs = 200, fat = 60, calories = 1740)

        val state = vm.uiState.value
        assertEquals(100, state.maxProtein)
        assertEquals(200, state.maxCarbs)
        assertEquals(60, state.maxFats)
        assertEquals(1740, state.maxCalories)
    }
}
