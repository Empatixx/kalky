package cz.krokviak.kalai.home

import cz.krokviak.kalai.common.entities.FoodItemEntity
import cz.krokviak.kalai.common.currentLocalDate
import kotlinx.datetime.LocalDate

data class MainUiState(
    val dailyStats: List<DailyStats> = emptyList(),
    val selectedBottomNavItem: Int = 0,
    val maxCalories: Int = 0,
    val currentCalories: Int = 0,
    val maxProtein: Int = 0,
    val currentProtein: Int = 0,
    val maxCarbs: Int = 0,
    val currentCarbs: Int = 0,
    val maxFats: Int = 0,
    val currentFats: Int = 0,
    val recentlyAddedItems: List<FoodItemEntity> = emptyList(),
    val loadingProgressForItems: Map<Long, Int> = emptyMap(),
    val currentDate: LocalDate = currentLocalDate(),
    val currentStreak: Int = 0
) {
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

data class DailyStats(
    val dayLabel: String,
    val protein: Int,
    val carbs: Int,
    val fat: Int
) {
    val totalCalories: Int get() = protein * 4 + carbs * 4 + fat * 9
}
