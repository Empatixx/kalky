package cz.krokviak.kalai.customfood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalai.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalai.common.entities.FoodItemEntity
import cz.krokviak.kalai.common.repo.FoodRepository
import cz.krokviak.kalai.network.OpenFoodFactsClient
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
            val items = foodRepository.getDistinctFoodsByName()
            _uiState.update { it.copy(historyItems = items, isLoading = false) }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            _uiState.update { it.copy(isLoading = true) }
            if (query.isBlank()) {
                val items = foodRepository.getDistinctFoodsByName()
                _uiState.update { it.copy(historyItems = items, apiResults = emptyList(), isLoading = false) }
            } else {
                val localDeferred = async { foodRepository.searchDistinctFoodsByName(query) }
                val apiDeferred = async { openFoodFactsClient.searchProducts(query) }
                val localItems = localDeferred.await()
                val apiItems = apiDeferred.await()
                _uiState.update { it.copy(historyItems = localItems, apiResults = apiItems, isLoading = false) }
            }
        }
    }

    fun addFromApi(product: OpenFoodFactsProduct) {
        viewModelScope.launch {
            val nutrients = product.nutriments
            val protein = nutrients?.proteins100g?.roundToInt() ?: 0
            val carbs = nutrients?.carbohydrates100g?.roundToInt() ?: 0
            val fat = nutrients?.fat100g?.roundToInt() ?: 0
            val calories = nutrients?.energyKcal100g?.roundToInt() ?: (protein * 4 + carbs * 4 + fat * 9)
            val now = Clock.System.now()
            val item = FoodItemEntity(
                name = product.productName ?: "",
                calories = calories,
                protein = protein,
                carbs = carbs,
                fat = fat,
                createdAt = now,
                updatedAt = now,
                localImagePath = "",
                loading = false
            )
            val newId = foodRepository.insertFoodItem(item)
            _foodAdded.emit(newId)
        }
    }

    fun toggleSelection(itemId: Long) {
        _uiState.update { state ->
            val newSelection = if (itemId in state.selectedItems) {
                state.selectedItems - itemId
            } else {
                state.selectedItems + itemId
            }
            state.copy(selectedItems = newSelection)
        }
    }

    fun addSelectedFoods() {
        viewModelScope.launch {
            val state = _uiState.value
            val now = Clock.System.now()
            for (itemId in state.selectedItems) {
                val item = state.historyItems.find { it.id == itemId } ?: continue
                val newItem = item.copy(
                    id = 0,
                    createdAt = now,
                    updatedAt = now,
                    loading = false,
                    localImagePath = ""
                )
                foodRepository.insertFoodItem(newItem)
            }
            _uiState.update { it.copy(selectedItems = emptySet()) }
            _foodAdded.emit(0)
            loadHistory()
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedItems = emptySet()) }
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
            val item = FoodItemEntity(
                name = state.name,
                calories = state.calories,
                protein = state.protein,
                carbs = state.carbs,
                fat = state.fat,
                createdAt = now,
                updatedAt = now,
                localImagePath = "",
                loading = false
            )
            val newId = foodRepository.insertFoodItem(item)
            _foodAdded.emit(newId)
        }
    }

    fun resetManualEntry() {
        _manualEntryState.value = ManualFoodEntryState()
    }
}
