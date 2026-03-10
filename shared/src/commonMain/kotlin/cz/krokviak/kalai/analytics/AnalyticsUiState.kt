package cz.krokviak.kalai.analytics

import cz.krokviak.kalai.common.currentLocalDate
import cz.krokviak.kalai.common.repo.WeightEntry
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

data class AnalyticsUiState(
    val weights: List<WeightEntry> = emptyList(),
    val caloriesBars: List<CaloriesBar> = emptyList(),
    val startDate: LocalDate = currentLocalDate().minus(6, DateTimeUnit.DAY),
    val endDate: LocalDate = currentLocalDate()
)

data class CaloriesBar(
    val label: String,
    val protein: Int,
    val carbs: Int,
    val fat: Int
) {
    val totalCalories: Int get() = protein * 4 + carbs * 4 + fat * 9
}
