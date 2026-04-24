package cz.krokviak.kalky.analytics

import androidx.compose.runtime.Immutable
import cz.krokviak.kalky.common.currentLocalDate
import cz.krokviak.kalky.common.repo.WeightEntry
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

@Immutable
data class AnalyticsUiState(
    val weights: PersistentList<WeightEntry> = persistentListOf(),
    val caloriesBars: PersistentList<CaloriesBar> = persistentListOf(),
    val startDate: LocalDate = currentLocalDate().minus(6, DateTimeUnit.DAY),
    val endDate: LocalDate = currentLocalDate()
)

@Immutable
data class CaloriesBar(
    val label: String,
    val protein: Int,
    val carbs: Int,
    val fat: Int
) {
    val totalCalories: Int get() = protein * 4 + carbs * 4 + fat * 9
}
