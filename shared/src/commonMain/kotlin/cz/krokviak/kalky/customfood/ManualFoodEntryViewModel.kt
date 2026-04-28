package cz.krokviak.kalky.customfood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalky.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.common.repo.FoodRepository
import cz.krokviak.kalky.common.utils.caloriesFromMacros
import cz.krokviak.kalky.network.OpenFoodFactsClient
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.math.roundToInt

class ManualFoodEntryViewModel(
    private val foodRepository: FoodRepository,
    private val openFoodFactsClient: OpenFoodFactsClient,
) : ViewModel() {

    private val _state = MutableStateFlow(ManualFoodEntryState())
    val state: StateFlow<ManualFoodEntryState> = _state

    private val _foodAdded = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val foodAdded: SharedFlow<Long> = _foodAdded

    private val _ingredientResults = MutableStateFlow<PersistentList<FoodItemEntity>>(persistentListOf())
    val ingredientResults: StateFlow<PersistentList<FoodItemEntity>> = _ingredientResults

    private val _ingredientApiResults = MutableStateFlow<PersistentList<OpenFoodFactsProduct>>(persistentListOf())
    val ingredientApiResults: StateFlow<PersistentList<OpenFoodFactsProduct>> = _ingredientApiResults

    private var ingredientSearchJob: Job? = null

    fun onNameChange(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun onProteinChange(value: Int) {
        _state.update {
            it.copy(protein = value, calories = caloriesFor(value, it.carbs, it.fat))
        }
    }

    fun onCarbsChange(value: Int) {
        _state.update {
            it.copy(carbs = value, calories = caloriesFor(it.protein, value, it.fat))
        }
    }

    fun onFatChange(value: Int) {
        _state.update {
            it.copy(fat = value, calories = caloriesFor(it.protein, it.carbs, value))
        }
    }

    fun submit() {
        viewModelScope.launch {
            val snapshot = _state.value
            val now = Clock.System.now()
            val item = FoodItemEntity(
                name = snapshot.name,
                calories = snapshot.calories,
                protein = snapshot.protein,
                carbs = snapshot.carbs,
                fat = snapshot.fat,
                createdAt = now,
                updatedAt = now,
                localImagePath = "",
                loading = false,
                isCustom = true,
            )
            val newId = foodRepository.insertFoodItem(item)
            _foodAdded.emit(newId)
        }
    }

    fun setSourceFoods(items: List<FoodItemEntity>) {
        val foods = items.toPersistentList()
        val portions = items.associate { it.id to 100 }.toPersistentMap()
        _state.update { state -> recalcTotals(state, foods, portions) }
    }

    fun addSourceFood(item: FoodItemEntity) {
        _state.update { state ->
            if (state.sourceFoods.any { it.id == item.id }) return@update state
            val newFoods = state.sourceFoods.add(item)
            val newPortions = state.sourcePortionGrams.put(item.id, 100)
            recalcTotals(state, newFoods, newPortions)
        }
    }

    fun removeSourceFood(foodId: Long) {
        _state.update { state ->
            val newFoods = state.sourceFoods.mutate { list -> list.removeAll { it.id == foodId } }
            val newPortions = state.sourcePortionGrams.remove(foodId)
            recalcTotals(state, newFoods, newPortions)
        }
    }

    fun updateSourcePortion(foodId: Long, grams: Int) {
        _state.update { state ->
            val newPortions = state.sourcePortionGrams.put(foodId, grams.coerceAtLeast(0))
            recalcTotals(state, state.sourceFoods, newPortions)
        }
    }

    fun addSourceFoodFromApi(product: OpenFoodFactsProduct) {
        val nutrients = product.nutriments
        val now = Clock.System.now()
        val food = FoodItemEntity(
            id = now.toEpochMilliseconds(),
            name = product.productName ?: "",
            calories = nutrients?.energyKcal100g?.roundToInt() ?: 0,
            protein = nutrients?.proteins100g?.roundToInt() ?: 0,
            carbs = nutrients?.carbohydrates100g?.roundToInt() ?: 0,
            fat = nutrients?.fat100g?.roundToInt() ?: 0,
            createdAt = now,
            updatedAt = now,
            loading = false,
        )
        addSourceFood(food)
    }

    fun searchIngredients(query: String) {
        ingredientSearchJob?.cancel()
        if (query.isBlank()) {
            _ingredientResults.value = persistentListOf()
            _ingredientApiResults.value = persistentListOf()
            return
        }
        ingredientSearchJob = viewModelScope.launch {
            delay(300)
            val localDeferred = async { foodRepository.searchDistinctFoodsByName(query) }
            val apiDeferred = async {
                runCatching { openFoodFactsClient.searchProducts(query, 10) }.getOrDefault(emptyList())
            }
            _ingredientResults.value = localDeferred.await().toPersistentList()
            _ingredientApiResults.value = apiDeferred.await().toPersistentList()
        }
    }

    fun reset() {
        _state.value = ManualFoodEntryState()
        _ingredientResults.value = persistentListOf()
        _ingredientApiResults.value = persistentListOf()
    }
}

private fun caloriesFor(protein: Int, carbs: Int, fat: Int): Int =
    caloriesFromMacros(protein, carbs, fat)

private fun recalcTotals(
    state: ManualFoodEntryState,
    foods: PersistentList<FoodItemEntity>,
    portions: kotlinx.collections.immutable.PersistentMap<Long, Int>,
): ManualFoodEntryState {
    val totalProtein = foods.sumOf { f -> ((f.protein * (portions[f.id] ?: 100)) / 100.0).roundToInt() }
    val totalCarbs = foods.sumOf { f -> ((f.carbs * (portions[f.id] ?: 100)) / 100.0).roundToInt() }
    val totalFat = foods.sumOf { f -> ((f.fat * (portions[f.id] ?: 100)) / 100.0).roundToInt() }
    return state.copy(
        sourceFoods = foods,
        sourcePortionGrams = portions,
        protein = totalProtein,
        carbs = totalCarbs,
        fat = totalFat,
        calories = caloriesFor(totalProtein, totalCarbs, totalFat),
    )
}
