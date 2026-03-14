package cz.krokviak.kalai.customfood

import cz.krokviak.kalai.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalai.common.entities.FoodItemEntity

data class CustomFoodUiState(
    val searchQuery: String = "",
    val customFoods: List<FoodItemEntity> = emptyList(),
    val historyItems: List<FoodItemEntity> = emptyList(),
    val apiResults: List<OpenFoodFactsProduct> = emptyList(),
    val isLoading: Boolean = false,
    val selectedItems: Set<Long> = emptySet(),
    val selectedApiProduct: OpenFoodFactsProduct? = null,
    val portionGrams: Int = 100
)

data class ManualFoodEntryState(
    val name: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val sourceFoods: List<FoodItemEntity> = emptyList(),
    val sourcePortionGrams: Map<Long, Int> = emptyMap()
)
