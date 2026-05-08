package cz.krokviak.kalky.scenes.analytics

import androidx.compose.runtime.Immutable
import cz.krokviak.kalky.core.common.currentLocalDate
import cz.krokviak.kalky.core.common.repo.WeightEntry
import cz.krokviak.kalky.core.common.utils.caloriesFromMacros
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
    val totalCalories: Int get() = caloriesFromMacros(protein, carbs, fat)
}
