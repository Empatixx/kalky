package cz.krokviak.kalky.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalky.common.FoodPhotoAnalyzer
import cz.krokviak.kalky.common.domain.AddFoodItemUseCase
import cz.krokviak.kalky.common.domain.DeleteFoodItemsUseCase
import cz.krokviak.kalky.common.domain.GetDailyMacrosUseCase
import cz.krokviak.kalky.common.domain.GetLatestNutrientSettingsUseCase
import cz.krokviak.kalky.common.domain.GetStreakUseCase
import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.common.error.toUiError
import cz.krokviak.kalky.db.DatabaseSeeder
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

class MainViewModel(
    private val getLatestSettings: GetLatestNutrientSettingsUseCase,
    foodPhotoAnalyzer: FoodPhotoAnalyzer,
    private val getDailyMacros: GetDailyMacrosUseCase,
    private val getStreak: GetStreakUseCase,
    private val addFoodItem: AddFoodItemUseCase,
    private val deleteFoodItems: DeleteFoodItemsUseCase,
    private val databaseSeeder: DatabaseSeeder,
    clock: Clock,
    private val seedMockData: Boolean = false,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MainUiState(dailyStats = generateFakeDailyStats(7))
    )
    val uiState: StateFlow<MainUiState> = _uiState

    private val photoCaptureController = PhotoCaptureController(
        scope = viewModelScope,
        state = _uiState,
        foodPhotoAnalyzer = foodPhotoAnalyzer,
        addFoodItem = addFoodItem,
        clock = clock,
        onMacrosChanged = ::recalculateMacrosFromState,
        onAnalysisFailed = { error -> _uiState.update { it.copy(error = error) } },
    )

    init {
        viewModelScope.launch {
            if (seedMockData) {
                withContext(Dispatchers.IO) { databaseSeeder.seedIfEmpty() }
            }
            val latestSettings = getLatestSettings()
            _uiState.update {
                it.copy(
                    maxProtein = latestSettings?.targetProtein ?: 0,
                    maxCarbs = latestSettings?.targetCarbs ?: 0,
                    maxFats = latestSettings?.targetFat ?: 0,
                    maxCalories = latestSettings?.targetCalories ?: 0,
                )
            }
            refreshStreak()
        }
    }

    private suspend fun refreshStreak() {
        val streak = getStreak()
        _uiState.update { it.copy(currentStreak = streak) }
    }

    fun addFoodItemFromBytes(imageBytes: ByteArray) =
        photoCaptureController.addFromBytes(imageBytes)

    fun addFoodItemFromBarcode(
        name: String,
        calories: Int,
        protein: Int,
        fat: Int,
        carbs: Int,
    ) = photoCaptureController.addFromBarcode(name, calories, protein, fat, carbs)

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    /** Recomputes totals from the in-memory list (used while items are mid-analysis). */
    private fun recalculateMacrosFromState() {
        _uiState.update { current ->
            current.copy(
                currentCalories = current.recentlyAddedItems.sumOf { it.calories },
                currentProtein = current.recentlyAddedItems.sumOf { it.protein },
                currentFats = current.recentlyAddedItems.sumOf { it.fat },
                currentCarbs = current.recentlyAddedItems.sumOf { it.carbs },
            )
        }
        viewModelScope.launch { refreshStreak() }
    }

    fun loadFoodItemsForDate(date: LocalDate) {
        viewModelScope.launch {
            runCatching { getDailyMacros(date) }
                .onSuccess { macros ->
                    _uiState.update { current ->
                        current.copy(
                            recentlyAddedItems = macros.items,
                            currentCalories = macros.totalCalories,
                            currentProtein = macros.totalProtein,
                            currentCarbs = macros.totalCarbs,
                            currentFats = macros.totalFat,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.toUiError()) }
                }
        }
    }

    private fun generateFakeDailyStats(days: Int): PersistentList<DailyStats> {
        val labels = listOf("Po", "Út", "St", "Čt", "Pá", "So", "Ne")
        return (0 until days).map { i ->
            val label = if (days == 7) labels[i % 7] else "Den ${i + 1}"
            DailyStats(
                dayLabel = label,
                protein = (20..100).random(),
                carbs = (50..200).random(),
                fat = (10..80).random(),
            )
        }.toPersistentList()
    }

    fun resetToToday() = onDateSelected(cz.krokviak.kalky.common.currentLocalDate())

    fun onDateSelected(date: LocalDate) {
        _uiState.update { current -> current.copy(currentDate = date) }
        loadFoodItemsForDate(date)
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
            _uiState.update { current ->
                current.copy(
                    recentlyAddedItems = current.recentlyAddedItems
                        .mutate { list -> list.removeAll { it.id in ids } },
                    selectedFoodIds = persistentSetOf(),
                )
            }
            recalculateMacrosFromState()
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
