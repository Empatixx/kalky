package cz.krokviak.kalai.home

import cz.krokviak.kalai.camera.entities.FoodItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class MainUiState(
    val currentScene: Scene = Scene.HOME,
    val analyticsRange: AnalyticsRange = AnalyticsRange.WEEK,
    val dailyStats: List<DailyStats> = emptyList(), // hypothetical data source
    val selectedBottomNavItem: Int = 0,
    val maxCalories: Int = 2250,     // Maximum calorie goal
    val currentCalories: Int = 0,    // Current calories consumed
    val maxProtein: Int = 100,        // Maximum protein in grams
    val currentProtein: Int = 0,     // Current protein consumed in grams
    val maxCarbs: Int = 100,          // Maximum carbohydrates in grams
    val currentCarbs: Int = 0,       // Current carbohydrates consumed in grams
    val maxFats: Int = 100,           // Maximum fats in grams
    val currentFats: Int = 0,        // Current fats consumed in grams
    val recentlyAddedItems: List<FoodItemEntity> = emptyList(),

    val loadingProgressForItems: Map<Long, Int> = emptyMap()

) {
    fun calorieDifference(): Int = maxCalories - currentCalories
    fun proteinDifference(): Int = maxProtein - currentProtein
    fun carbsDifference(): Int = maxCarbs - currentCarbs
    fun fatsDifference(): Int = maxFats - currentFats

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