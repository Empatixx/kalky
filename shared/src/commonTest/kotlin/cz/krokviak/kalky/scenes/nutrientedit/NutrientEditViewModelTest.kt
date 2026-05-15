package cz.krokviak.kalky.scenes.nutrientedit

import cz.krokviak.kalky.core.common.domain.GetLatestNutrientSettingsUseCase
import cz.krokviak.kalky.core.common.domain.UpdateNutrientSettingsUseCase
import cz.krokviak.kalky.core.common.entities.NutrientSettingEntity
import cz.krokviak.kalky.core.common.repo.NutrientSettingRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class NutrientEditViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads latest settings into uiState`() = runTest(dispatcher) {
        val repo = FakeNutrientSettingRepo(
            stored = NutrientSettingEntity(
                targetProtein = 120,
                targetCarbs = 250,
                targetFat = 80,
                targetCalories = 2200,
            )
        )
        val viewModel = NutrientEditViewModel(
            getLatestSettings = GetLatestNutrientSettingsUseCase(repo),
            updateSettings = UpdateNutrientSettingsUseCase(repo),
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(120, state.protein)
        assertEquals(250, state.carbs)
        assertEquals(80, state.fat)
        assertEquals(2200, state.calories)
    }

    @Test
    fun `onProteinChange updates state immediately and recomputes calories`() = runTest(dispatcher) {
        val repo = FakeNutrientSettingRepo()
        val viewModel = NutrientEditViewModel(
            getLatestSettings = GetLatestNutrientSettingsUseCase(repo),
            updateSettings = UpdateNutrientSettingsUseCase(repo),
        )
        advanceUntilIdle()

        viewModel.onProteinChange(50)

        val state = viewModel.uiState.value
        assertEquals(50, state.protein)

        assertEquals(200, state.calories)
    }

    @Test
    fun `save is debounced - only persisted after 300ms of inactivity`() = runTest(dispatcher) {
        val repo = FakeNutrientSettingRepo()
        val viewModel = NutrientEditViewModel(
            getLatestSettings = GetLatestNutrientSettingsUseCase(repo),
            updateSettings = UpdateNutrientSettingsUseCase(repo),
        )
        advanceUntilIdle()
        repo.inserts.clear()

        viewModel.onProteinChange(10)
        advanceTimeBy(100)
        viewModel.onProteinChange(20)
        advanceTimeBy(100)
        viewModel.onProteinChange(30)

        assertEquals(0, repo.inserts.size)

        advanceTimeBy(300)
        advanceUntilIdle()

        assertEquals(1, repo.inserts.size)
        assertEquals(30, repo.inserts.last().targetProtein)
    }
}

private class FakeNutrientSettingRepo(
    private val stored: NutrientSettingEntity? = null,
) : NutrientSettingRepo {
    val inserts = mutableListOf<NutrientSettingEntity>()
    override suspend fun getLatestNutrientSettings(): NutrientSettingEntity? = stored
    override suspend fun insertNutrientSettings(entity: NutrientSettingEntity) {
        inserts += entity
    }
}
