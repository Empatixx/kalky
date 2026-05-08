package cz.krokviak.kalky.scenes.home

import androidx.compose.runtime.Immutable
import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.currentLocalDate
import cz.krokviak.kalky.core.common.error.UiError
import cz.krokviak.kalky.core.common.utils.caloriesFromMacros
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.datetime.LocalDate

@Immutable
data class MainUiState(
    val dailyStats: PersistentList<DailyStats> = persistentListOf(),
    val selectedBottomNavItem: Int = 0,
    val maxCalories: Int = 0,
    val currentCalories: Int = 0,
    val maxProtein: Int = 0,
    val currentProtein: Int = 0,
    val maxCarbs: Int = 0,
    val currentCarbs: Int = 0,
    val maxFats: Int = 0,
    val currentFats: Int = 0,
    val recentlyAddedItems: PersistentList<FoodItemEntity> = persistentListOf(),
    val loadingItems: PersistentSet<Long> = persistentSetOf(),
    val currentDate: LocalDate = currentLocalDate(),
    val currentStreak: Int = 0,
    val selectedFoodIds: PersistentSet<Long> = persistentSetOf(),
    val error: UiError? = null
) {
    val isSelectionMode: Boolean get() = selectedFoodIds.isNotEmpty()
    val isToday: Boolean get() = currentDate == currentLocalDate()
    fun calorieRatio(): Float = amountRatio(currentCalories, maxCalories)
    fun proteinRatio(): Float = amountRatio(currentProtein, maxProtein)
    fun carbsRatio(): Float = amountRatio(currentCarbs, maxCarbs)
    fun fatsRatio(): Float = amountRatio(currentFats, maxFats)
    fun amountRatio(current: Int, max: Int): Float {
        if (max <= 0) return 0f
        return (current.toFloat() / max.toFloat()).coerceIn(0f, 1f)
    }
}

@Immutable
data class DailyStats(
    val dayLabel: String,
    val protein: Int,
    val carbs: Int,
    val fat: Int
) {
    val totalCalories: Int get() = caloriesFromMacros(protein, carbs, fat)
}
