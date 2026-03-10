package cz.krokviak.kalai.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalai.common.currentLocalDate
import cz.krokviak.kalai.common.repo.FoodRepository
import cz.krokviak.kalai.common.repo.PersonalInfoRepo
import cz.krokviak.kalai.common.toCzechShortName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class AnalyticsViewModel(
    private val foodRepository: FoodRepository,
    private val personalInfoRepo: PersonalInfoRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState

    init {
        loadData()
    }

    fun setStartDate(date: LocalDate) {
        _uiState.update { it.copy(startDate = date) }
        loadData()
    }

    fun setEndDate(date: LocalDate) {
        _uiState.update { it.copy(endDate = date) }
        loadData()
    }

    private fun loadData() {
        val state = _uiState.value
        val start = state.startDate
        val end = state.endDate
        val days = daysBetween(start, end)

        viewModelScope.launch {
            val bars = getBarsForRange(start, end, days)
            val weights = personalInfoRepo.getWeightsInRange(start, end)

            _uiState.update {
                it.copy(
                    weights = weights,
                    caloriesBars = bars
                )
            }
        }
    }

    private suspend fun getBarsForRange(
        start: LocalDate,
        end: LocalDate,
        days: Int
    ): List<CaloriesBar> {
        val dailyTotals = foodRepository.getDailyMacroTotalsInRange(
            start.toString(),
            end.toString()
        )
        val totalsByDate = dailyTotals.associateBy { it.day }

        return (0 until days).map { i ->
            val date = start.plus(i, DateTimeUnit.DAY)
            val label = if (days <= 14) {
                date.dayOfWeek.toCzechShortName()
            } else {
                "${date.dayOfMonth}.${date.monthNumber}."
            }
            val dayTotals = totalsByDate[date]
            CaloriesBar(
                label = label,
                protein = dayTotals?.totalProtein ?: 0,
                carbs = dayTotals?.totalCarbs ?: 0,
                fat = dayTotals?.totalFat ?: 0
            )
        }
    }

    private fun daysBetween(start: LocalDate, end: LocalDate): Int {
        return (end.toEpochDays() - start.toEpochDays() + 1).coerceAtLeast(1)
    }
}
