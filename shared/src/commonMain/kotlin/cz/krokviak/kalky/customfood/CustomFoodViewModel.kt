package cz.krokviak.kalky.customfood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalky.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.common.repo.FoodRepository
import cz.krokviak.kalky.network.OpenFoodFactsClient
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
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

class CustomFoodViewModel(
    private val foodRepository: FoodRepository,
    private val openFoodFactsClient: OpenFoodFactsClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomFoodUiState())
    val uiState: StateFlow<CustomFoodUiState> = _uiState

    private val _manualEntryState = MutableStateFlow(ManualFoodEntryState())
    val manualEntryState: StateFlow<ManualFoodEntryState> = _manualEntryState

    private val _foodAdded = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val foodAdded: SharedFlow<Long> = _foodAdded

    private var searchJob: Job? = null

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val custom = foodRepository.getCustomFoods()
            val items = foodRepository.getDistinctFoodsByName()
            _uiState.update {
                it.copy(
                    customFoods = custom.toPersistentList(),
                    historyItems = items.toPersistentList(),
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            _uiState.update { it.copy(isLoading = true) }
            if (query.isBlank()) {
                val custom = foodRepository.getCustomFoods()
                val items = foodRepository.getDistinctFoodsByName()
                _uiState.update {
                    it.copy(
                        customFoods = custom.toPersistentList(),
                        historyItems = items.toPersistentList(),
                        apiResults = persistentListOf(),
                        isLoading = false
                    )
                }
            } else {
                val customDeferred = async { foodRepository.searchCustomFoods(query) }
                val localDeferred = async { foodRepository.searchDistinctFoodsByName(query) }
                val apiDeferred = async { openFoodFactsClient.searchProducts(query) }
                val customItems = customDeferred.await()
                val localItems = localDeferred.await()
                val apiItems = apiDeferred.await()
                _uiState.update {
                    it.copy(
                        customFoods = customItems.toPersistentList(),
                        historyItems = localItems.toPersistentList(),
                        apiResults = apiItems.toPersistentList(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectApiProduct(product: OpenFoodFactsProduct) {
        _uiState.update { it.copy(selectedApiProduct = product, portionGrams = 100) }
    }

    fun dismissPortionPicker() {
        _uiState.update { it.copy(selectedApiProduct = null) }
    }

    fun setPortionGrams(grams: Int) {
        _uiState.update { it.copy(portionGrams = grams.coerceIn(1, 9999)) }
    }

    fun confirmAddApiProduct() {
        val product = _uiState.value.selectedApiProduct ?: return
        val grams = _uiState.value.portionGrams
        viewModelScope.launch {
            val nutrients = product.nutriments
            val factor = grams / 100.0
            val protein = ((nutrients?.proteins100g ?: 0.0) * factor).roundToInt()
            val carbs = ((nutrients?.carbohydrates100g ?: 0.0) * factor).roundToInt()
            val fat = ((nutrients?.fat100g ?: 0.0) * factor).roundToInt()
            val calories = ((nutrients?.energyKcal100g ?: 0.0) * factor).roundToInt()
            val now = Clock.System.now()
            val item = FoodItemEntity(
                name = product.productName ?: "",
                calories = if (calories > 0) calories else (protein * 4 + carbs * 4 + fat * 9),
                protein = protein,
                carbs = carbs,
                fat = fat,
                portion = grams,
                createdAt = now,
                updatedAt = now,
                localImagePath = "",
                loading = false
            )
            val newId = foodRepository.insertFoodItem(item)
            _uiState.update {
                it.copy(
                    selectedApiProduct = null,
                    searchQuery = "",
                    apiResults = persistentListOf()
                )
            }
            _foodAdded.emit(newId)
        }
    }

    fun toggleSelection(itemId: Long) {
        _uiState.update { state ->
            val newSelection = if (itemId in state.selectedItems) {
                state.selectedItems.remove(itemId)
            } else {
                state.selectedItems.add(itemId)
            }
            state.copy(selectedItems = newSelection)
        }
    }

    fun addSelectedFoods() {
        viewModelScope.launch {
            val state = _uiState.value
            val now = Clock.System.now()
            for (itemId in state.selectedItems) {
                val allItems = state.customFoods + state.historyItems
                val item = allItems.find { it.id == itemId } ?: continue
                val newItem = item.copy(
                    id = 0,
                    createdAt = now,
                    updatedAt = now,
                    loading = false,
                    localImagePath = ""
                )
                foodRepository.insertFoodItem(newItem)
            }
            _uiState.update {
                it.copy(
                    selectedItems = persistentSetOf(),
                    searchQuery = "",
                    apiResults = persistentListOf()
                )
            }
            _foodAdded.emit(0)
            loadHistory()
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedItems = persistentSetOf()) }
    }

    fun onNameChange(name: String) {
        _manualEntryState.update { it.copy(name = name) }
    }

    fun onManualProteinChange(value: Int) {
        _manualEntryState.update {
            it.copy(
                protein = value,
                calories = value * 4 + it.carbs * 4 + it.fat * 9
            )
        }
    }

    fun onManualCarbsChange(value: Int) {
        _manualEntryState.update {
            it.copy(
                carbs = value,
                calories = it.protein * 4 + value * 4 + it.fat * 9
            )
        }
    }

    fun onManualFatChange(value: Int) {
        _manualEntryState.update {
            it.copy(
                fat = value,
                calories = it.protein * 4 + it.carbs * 4 + value * 9
            )
        }
    }

    fun submitManualEntry() {
        viewModelScope.launch {
            val state = _manualEntryState.value
            val now = Clock.System.now()
            val isCustom = true
            val item = FoodItemEntity(
                name = state.name,
                calories = state.calories,
                protein = state.protein,
                carbs = state.carbs,
                fat = state.fat,
                createdAt = now,
                updatedAt = now,
                localImagePath = "",
                loading = false,
                isCustom = isCustom
            )
            val newId = foodRepository.insertFoodItem(item)
            _foodAdded.emit(newId)
        }
    }

    fun setSourceFoods(items: List<FoodItemEntity>) {
        val defaultPortions = items.associate { it.id to 100 }.toPersistentMap()
        _manualEntryState.update {
            it.copy(
                sourceFoods = items.toPersistentList(),
                sourcePortionGrams = defaultPortions
            )
        }
    }

    fun addSourceFood(item: FoodItemEntity) {
        _manualEntryState.update { state ->
            if (state.sourceFoods.any { it.id == item.id }) return@update state
            val newFoods = state.sourceFoods.add(item)
            val newPortions = state.sourcePortionGrams.put(item.id, 100)
            val totalProtein = newFoods.sumOf { f -> ((f.protein * (newPortions[f.id] ?: 100)) / 100.0).roundToInt() }
            val totalCarbs = newFoods.sumOf { f -> ((f.carbs * (newPortions[f.id] ?: 100)) / 100.0).roundToInt() }
            val totalFat = newFoods.sumOf { f -> ((f.fat * (newPortions[f.id] ?: 100)) / 100.0).roundToInt() }
            state.copy(
                sourceFoods = newFoods,
                sourcePortionGrams = newPortions,
                protein = totalProtein,
                carbs = totalCarbs,
                fat = totalFat,
                calories = totalProtein * 4 + totalCarbs * 4 + totalFat * 9
            )
        }
    }

    fun removeSourceFood(foodId: Long) {
        _manualEntryState.update { state ->
            val newFoods = state.sourceFoods.mutate { list -> list.removeAll { it.id == foodId } }
            val newPortions = state.sourcePortionGrams.remove(foodId)
            val totalProtein = newFoods.sumOf { f -> ((f.protein * (newPortions[f.id] ?: 100)) / 100.0).roundToInt() }
            val totalCarbs = newFoods.sumOf { f -> ((f.carbs * (newPortions[f.id] ?: 100)) / 100.0).roundToInt() }
            val totalFat = newFoods.sumOf { f -> ((f.fat * (newPortions[f.id] ?: 100)) / 100.0).roundToInt() }
            state.copy(
                sourceFoods = newFoods,
                sourcePortionGrams = newPortions,
                protein = totalProtein,
                carbs = totalCarbs,
                fat = totalFat,
                calories = totalProtein * 4 + totalCarbs * 4 + totalFat * 9
            )
        }
    }

    private var ingredientSearchJob: Job? = null
    private val _ingredientResults = MutableStateFlow<PersistentList<FoodItemEntity>>(persistentListOf())
    val ingredientResults: StateFlow<PersistentList<FoodItemEntity>> = _ingredientResults

    private val _ingredientApiResults = MutableStateFlow<PersistentList<OpenFoodFactsProduct>>(persistentListOf())
    val ingredientApiResults: StateFlow<PersistentList<OpenFoodFactsProduct>> = _ingredientApiResults

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
            val apiDeferred = async { openFoodFactsClient.searchProducts(query, 10) }
            _ingredientResults.value = localDeferred.await().toPersistentList()
            _ingredientApiResults.value = apiDeferred.await().toPersistentList()
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
            loading = false
        )
        addSourceFood(food)
    }

    fun updateSourcePortion(foodId: Long, grams: Int) {
        _manualEntryState.update { state ->
            val newPortions = state.sourcePortionGrams.put(foodId, grams.coerceAtLeast(0))
            val totalProtein = state.sourceFoods.sumOf { food ->
                val portion = newPortions[food.id] ?: 100
                (food.protein * portion / 100.0).roundToInt()
            }
            val totalCarbs = state.sourceFoods.sumOf { food ->
                val portion = newPortions[food.id] ?: 100
                (food.carbs * portion / 100.0).roundToInt()
            }
            val totalFat = state.sourceFoods.sumOf { food ->
                val portion = newPortions[food.id] ?: 100
                (food.fat * portion / 100.0).roundToInt()
            }
            state.copy(
                sourcePortionGrams = newPortions,
                protein = totalProtein,
                carbs = totalCarbs,
                fat = totalFat,
                calories = totalProtein * 4 + totalCarbs * 4 + totalFat * 9
            )
        }
    }

    fun resetManualEntry() {
        _manualEntryState.value = ManualFoodEntryState()
    }
}

