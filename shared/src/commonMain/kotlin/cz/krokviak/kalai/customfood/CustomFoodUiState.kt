package cz.krokviak.kalai.customfood

import cz.krokviak.kalai.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalai.common.entities.FoodItemEntity

data class CustomFoodUiState(
    val searchQuery: String = "",
    val historyItems: List<FoodItemEntity> = emptyList(),
    val apiResults: List<OpenFoodFactsProduct> = emptyList(),
    val isLoading: Boolean = false,
    val selectedItems: Set<Long> = emptySet()
)

data class ManualFoodEntryState(
    val name: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0
)
