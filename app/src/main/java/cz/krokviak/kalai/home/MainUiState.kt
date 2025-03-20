package cz.krokviak.kalai.home

import cz.krokviak.kalai.analytics.AnalyticsRange
import cz.krokviak.kalai.common.entities.FoodItemEntity
import org.threeten.bp.LocalDate


data class MainUiState(
    val analyticsRange: AnalyticsRange = AnalyticsRange.WEEK,
    val dailyStats: List<DailyStats> = emptyList(), // hypothetical data source
    val selectedBottomNavItem: Int = 0,
    val maxCalories: Int = 0,     // Maximum calorie goal
    val currentCalories: Int = 0,    // Current calories consumed
    val maxProtein: Int = 0,        // Maximum protein in grams
    val currentProtein: Int = 0,     // Current protein consumed in grams
    val maxCarbs: Int = 0,          // Maximum carbohydrates in grams
    val currentCarbs: Int = 0,       // Current carbohydrates consumed in grams
    val maxFats: Int = 0,           // Maximum fats in grams
    val currentFats: Int = 0,        // Current fats consumed in grams

    val recentlyAddedItems: List<FoodItemEntity> = emptyList(),
    val loadingProgressForItems: Map<Long, Int> = emptyMap(),

    val currentDate: LocalDate = LocalDate.now()

) {
    fun calorieRatio(): Float = amountRatio(currentCalories, maxCalories)
    fun proteinRatio(): Float = amountRatio(currentProtein, maxProtein)
    fun carbsRatio(): Float = amountRatio(currentCarbs, maxCarbs)
    fun fatsRatio(): Float = amountRatio(currentFats, maxFats)
    fun amountRatio(current: Int, max: Int): Float {
        if (max <= 0) return 0f
        return (current.toFloat() / max.toFloat()).coerceIn(0f, 1f)
    }
}
// A simple data class for each day's macros
data class DailyStats(
    val dayLabel: String, // e.g. "M", "T", "W" or a date string
    val protein: Int,
    val carbs: Int,
    val fat: Int
) {
    val totalCalories: Int get() = protein * 4 + carbs * 4 + fat * 9
}