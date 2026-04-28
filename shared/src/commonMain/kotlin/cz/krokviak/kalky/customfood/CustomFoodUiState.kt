package cz.krokviak.kalky.customfood

import androidx.compose.runtime.Immutable
import cz.krokviak.kalky.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalky.nutrientedit.MacroField
import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.common.error.UiError
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf

@Immutable
data class CustomFoodUiState(
    val searchQuery: String = "",
    val customFoods: PersistentList<FoodItemEntity> = persistentListOf(),
    val historyItems: PersistentList<FoodItemEntity> = persistentListOf(),
    val apiResults: PersistentList<OpenFoodFactsProduct> = persistentListOf(),
    val isLoading: Boolean = false,
    val selectedItems: PersistentSet<Long> = persistentSetOf(),
    val selectedApiProduct: OpenFoodFactsProduct? = null,
    val portionGrams: Int = 100,
    val error: UiError? = null
)

@Immutable
data class ManualFoodEntryState(
    val name: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val sourceFoods: PersistentList<FoodItemEntity> = persistentListOf(),
    val sourcePortionGrams: PersistentMap<Long, Int> = persistentMapOf(),
    val activeField: MacroField? = null,
    val ingredientResults: PersistentList<FoodItemEntity> = persistentListOf(),
    val ingredientApiResults: PersistentList<OpenFoodFactsProduct> = persistentListOf(),
)
