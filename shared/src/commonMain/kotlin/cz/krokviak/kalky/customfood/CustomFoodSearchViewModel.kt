package cz.krokviak.kalky.customfood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalky.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalky.common.domain.AddFoodItemUseCase
import cz.krokviak.kalky.common.domain.GetFoodLibraryUseCase
import cz.krokviak.kalky.common.domain.SearchFoodsUseCase
import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.common.error.UiError
import cz.krokviak.kalky.network.OpenFoodFactsClient
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class CustomFoodSearchViewModel(
    private val getFoodLibrary: GetFoodLibraryUseCase,
    private val searchFoods: SearchFoodsUseCase,
    private val openFoodFactsClient: OpenFoodFactsClient,
    private val addFoodItem: AddFoodItemUseCase,
    private val clock: Clock,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomFoodUiState())
    val uiState: StateFlow<CustomFoodUiState> = _uiState

    private val _foodAdded = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val foodAdded: SharedFlow<Long> = _foodAdded

    private var searchJob: Job? = null

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val library = getFoodLibrary()
            _uiState.update {
                it.copy(
                    customFoods = library.custom.toPersistentList(),
                    historyItems = library.history.toPersistentList(),
                    isLoading = false,
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
            if (query.isBlank()) reloadLibrary() else runRemoteSearch(query)
        }
    }

    private suspend fun reloadLibrary() {
        val library = getFoodLibrary()
        _uiState.update {
            it.copy(
                customFoods = library.custom.toPersistentList(),
                historyItems = library.history.toPersistentList(),
                apiResults = persistentListOf(),
                isLoading = false,
            )
        }
    }

    private suspend fun runRemoteSearch(query: String) = coroutineScope {
        val localDeferred = async { searchFoods(query) }
        val apiDeferred = async { runCatching { openFoodFactsClient.searchProducts(query) } }
        val localResult = localDeferred.await()
        val apiResult = apiDeferred.await()
        val apiError = if (apiResult.isFailure) UiError.ProductSearch else null
        _uiState.update {
            it.copy(
                customFoods = localResult.custom.toPersistentList(),
                historyItems = localResult.history.toPersistentList(),
                apiResults = apiResult.getOrDefault(emptyList()).toPersistentList(),
                isLoading = false,
                error = apiError ?: it.error,
            )
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
            val scaled = product.nutriments.scaledTo(grams)
            val now = clock.now()
            val item = FoodItemEntity(
                name = product.productName ?: "",
                calories = scaled.calories,
                protein = scaled.protein,
                carbs = scaled.carbs,
                fat = scaled.fat,
                portion = grams,
                createdAt = now,
                updatedAt = now,
                localImagePath = "",
                loading = false,
            )
            val newId = addFoodItem(item)
            _uiState.update {
                it.copy(
                    selectedApiProduct = null,
                    searchQuery = "",
                    apiResults = persistentListOf(),
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
            val now = clock.now()
            for (itemId in state.selectedItems) {
                val allItems = state.customFoods + state.historyItems
                val item = allItems.find { it.id == itemId } ?: continue
                val newItem = item.copy(
                    id = 0,
                    createdAt = now,
                    updatedAt = now,
                    loading = false,
                    localImagePath = "",
                )
                addFoodItem(newItem)
            }
            _uiState.update {
                it.copy(
                    selectedItems = persistentSetOf(),
                    searchQuery = "",
                    apiResults = persistentListOf(),
                )
            }
            _foodAdded.emit(0)
            loadHistory()
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedItems = persistentSetOf()) }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
