package cz.krokviak.kalky.scenes.analytics

import cz.krokviak.kalky.core.common.domain.BuildCaloriesBarsUseCase
import cz.krokviak.kalky.core.common.domain.GetWeightsInRangeUseCase
import cz.krokviak.kalky.core.common.repo.WeightEntry
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matching
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import dev.mokkery.verify.VerifyMode
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() { Dispatchers.setMain(dispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    private fun buildVm(
        bars: List<CaloriesBar> = listOf(CaloriesBar("Po", 10, 20, 5)),
        weights: List<WeightEntry> = listOf(WeightEntry(LocalDate(2026, 5, 8), 80.0)),
    ): AnalyticsViewModel {
        val barsUseCase = mock<BuildCaloriesBarsUseCase> {
            everySuspend { invoke(any(), any(), any()) } returns bars.toPersistentListLocal()
        }
        val weightsUseCase = mock<GetWeightsInRangeUseCase> {
            everySuspend { invoke(any(), any()) } returns weights
        }
        return AnalyticsViewModel(barsUseCase, weightsUseCase)
    }

    private fun List<CaloriesBar>.toPersistentListLocal() =
        persistentListOf<CaloriesBar>().addAll(this)

    @Test
    fun init_loadsBarsAndWeights() = runTest(dispatcher) {
        val vm = buildVm()
        advanceUntilIdle()

        val s = vm.uiState.value
        assertEquals(1, s.caloriesBars.size)
        assertEquals("Po", s.caloriesBars.first().label)
        assertEquals(1, s.weights.size)
    }

    @Test
    fun setStartDate_updatesStartAndReloads() = runTest(dispatcher) {
        val vm = buildVm()
        advanceUntilIdle()

        val newStart = LocalDate(2026, 4, 1)
        vm.setStartDate(newStart)
        advanceUntilIdle()

        assertEquals(newStart, vm.uiState.value.startDate)
    }

    @Test
    fun setEndDate_updatesEndAndReloads() = runTest(dispatcher) {
        val vm = buildVm()
        advanceUntilIdle()

        val newEnd = LocalDate(2026, 6, 1)
        vm.setEndDate(newEnd)
        advanceUntilIdle()

        assertEquals(newEnd, vm.uiState.value.endDate)
    }

    @Test
    fun setStartDate_passesNewStartToBarsUseCase() = runTest(dispatcher) {
        val barsUseCase = mock<BuildCaloriesBarsUseCase> {
            everySuspend { invoke(any(), any(), any()) } returns persistentListOf()
        }
        val weightsUseCase = mock<GetWeightsInRangeUseCase> {
            everySuspend { invoke(any(), any()) } returns emptyList()
        }
        val vm = AnalyticsViewModel(barsUseCase, weightsUseCase)
        advanceUntilIdle()

        val newStart = LocalDate(2026, 1, 1)
        vm.setStartDate(newStart)
        advanceUntilIdle()

        verifySuspend(VerifyMode.atLeast(1)) {
            barsUseCase.invoke(matching { it == newStart }, any(), any())
        }
    }
}
