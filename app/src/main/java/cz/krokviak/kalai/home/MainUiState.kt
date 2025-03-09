package cz.krokviak.kalai.home

import cz.krokviak.kalai.camera.entities.FoodItemEntity
import kotlin.math.abs


data class MainUiState(
    val selectedBottomNavItem: Int = 0,
    val maxCalories: Int = 2250,     // Maximum calorie goal
    val currentCalories: Int = 0,    // Current calories consumed
    val maxProtein: Int = 100,        // Maximum protein in grams
    val currentProtein: Int = 0,     // Current protein consumed in grams
    val maxCarbs: Int = 100,          // Maximum carbohydrates in grams
    val currentCarbs: Int = 0,       // Current carbohydrates consumed in grams
    val maxFats: Int = 100,           // Maximum fats in grams
    val currentFats: Int = 0,        // Current fats consumed in grams
    val recentlyAddedItems: List<FoodItemEntity> = emptyList()
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