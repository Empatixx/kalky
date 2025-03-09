package cz.krokviak.kalai.home

import cz.krokviak.kalai.camera.entities.FoodItemEntity


data class MainUiState(
    val selectedBottomNavItem: Int = 0,
    val maxCalories: Int = 1250,     // Maximum calorie goal
    val currentCalories: Int = 0,    // Current calories consumed
    val maxProtein: Int = 45,        // Maximum protein in grams
    val currentProtein: Int = 0,     // Current protein consumed in grams
    val maxCarbs: Int = 89,          // Maximum carbohydrates in grams
    val currentCarbs: Int = 0,       // Current carbohydrates consumed in grams
    val maxFats: Int = 48,           // Maximum fats in grams
    val currentFats: Int = 0,        // Current fats consumed in grams
    val recentlyAddedItems: List<FoodItemEntity> = emptyList()
) {
    fun caloriesLeft(): Int = maxCalories - currentCalories
    fun proteinLeft(): Int = maxProtein - currentProtein
    fun carbsLeft(): Int = maxCarbs - currentCarbs
    fun fatsLeft(): Int = maxFats - currentFats
}
