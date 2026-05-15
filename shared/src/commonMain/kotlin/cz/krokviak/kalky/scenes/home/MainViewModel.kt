package cz.krokviak.kalky.scenes.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalky.core.common.FoodPhotoAnalyzer
import cz.krokviak.kalky.core.common.domain.AddFoodItemUseCase
import cz.krokviak.kalky.core.common.domain.DeleteFoodItemsUseCase
import cz.krokviak.kalky.core.common.domain.GetLatestNutrientSettingsUseCase
import cz.krokviak.kalky.core.common.domain.GetStreakUseCase
import cz.krokviak.kalky.core.common.domain.ObserveDailyMacrosUseCase
import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.db.DatabaseSeeder
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

class MainViewModel(
    private val getLatestSettings: GetLatestNutrientSettingsUseCase,
    foodPhotoAnalyzer: FoodPhotoAnalyzer,
    private val observeDailyMacros: ObserveDailyMacrosUseCase,
    private val getStreak: GetStreakUseCase,
    private val addFoodItem: AddFoodItemUseCase,
    private val deleteFoodItems: DeleteFoodItemsUseCase,
    private val databaseSeeder: DatabaseSeeder,
    clock: Clock,
    private val seedMockData: Boolean = false,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    private val photoCaptureController = PhotoCaptureController(
        scope = viewModelScope,
        state = _uiState,
        foodPhotoAnalyzer = foodPhotoAnalyzer,
        addFoodItem = addFoodItem,
        clock = clock,
        onAnalysisFailed = { error -> _uiState.update { it.copy(error = error) } },
    )

    init {
        viewModelScope.launch {
            if (seedMockData) {
                withContext(Dispatchers.IO) { databaseSeeder.seedIfEmpty() }
            }
            applyLatestNutrientSettings()
        }
        observeMacrosForCurrentDate()
    }

    private fun observeMacrosForCurrentDate() {
        viewModelScope.launch {
            _uiState
                .map { it.currentDate }
                .distinctUntilChanged()
                .collectLatest { date ->
                    observeDailyMacros(date).collect { macros ->
                        _uiState.update { current ->
                            current.copy(
                                recentlyAddedItems = macros.items,
                                currentCalories = macros.totalCalories,
                                currentProtein = macros.totalProtein,
                                currentCarbs = macros.totalCarbs,
                                currentFats = macros.totalFat,
                            )
                        }
                        refreshStreak()
                    }
                }
        }
    }

    fun refreshNutrientSettings() {
        viewModelScope.launch { applyLatestNutrientSettings() }
    }

    private suspend fun applyLatestNutrientSettings() {
        val latestSettings = getLatestSettings()
        _uiState.update {
            it.copy(
                maxProtein = latestSettings?.targetProtein ?: 0,
                maxCarbs = latestSettings?.targetCarbs ?: 0,
                maxFats = latestSettings?.targetFat ?: 0,
                maxCalories = latestSettings?.targetCalories ?: 0,
            )
        }
    }

    private suspend fun refreshStreak() {
        val streak = getStreak()
        _uiState.update { it.copy(currentStreak = streak) }
    }

    fun addFoodItemFromBytes(imageBytes: ByteArray) {
        resetToToday()
        photoCaptureController.addFromBytes(imageBytes)
    }

    fun addFoodItemFromBarcode(
        name: String,
        calories: Int,
        protein: Int,
        fat: Int,
        carbs: Int,
    ) {
        resetToToday()
        photoCaptureController.addFromBarcode(name, calories, protein, fat, carbs)
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetToToday() = onDateSelected(cz.krokviak.kalky.core.common.currentLocalDate())

    fun onDateSelected(date: LocalDate) {
        _uiState.update { current -> current.copy(currentDate = date) }
    }

    fun toggleFoodSelection(id: Long) {
        _uiState.update { current ->
            val newSelection = if (id in current.selectedFoodIds) {
                current.selectedFoodIds.remove(id)
            } else {
                current.selectedFoodIds.add(id)
            }
            current.copy(selectedFoodIds = newSelection)
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedFoodIds = persistentSetOf()) }
    }

    fun deleteSelectedFoods() {
        viewModelScope.launch {
            val ids = _uiState.value.selectedFoodIds
            deleteFoodItems(ids)
            _uiState.update { it.copy(selectedFoodIds = persistentSetOf()) }
        }
    }

    fun getSelectedFoodItems(): List<FoodItemEntity> {
        val state = _uiState.value
        return state.recentlyAddedItems.filter { it.id in state.selectedFoodIds }
    }

    fun updateNutrientSettings(protein: Int, carbs: Int, fat: Int, calories: Int) {
        _uiState.update { current ->
            current.copy(
                maxProtein = protein,
                maxCarbs = carbs,
                maxFats = fat,
                maxCalories = calories,
            )
        }
    }
}
