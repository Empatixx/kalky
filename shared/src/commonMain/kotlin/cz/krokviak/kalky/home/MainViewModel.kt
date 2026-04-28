package cz.krokviak.kalky.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalky.common.FoodPhotoAnalyzer
import cz.krokviak.kalky.common.domain.AddFoodItemUseCase
import cz.krokviak.kalky.common.domain.DeleteFoodItemsUseCase
import cz.krokviak.kalky.common.domain.GetLatestNutrientSettingsUseCase
import cz.krokviak.kalky.common.domain.GetStreakUseCase
import cz.krokviak.kalky.common.domain.ObserveDailyMacrosUseCase
import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.db.DatabaseSeeder
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
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

    /**
     * Subscribes to the daily macros flow for whichever date is currently selected.
     * `collectLatest` cancels the previous date's collector when the user changes day,
     * so we never have stale items lingering in state.
     */
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

    /** Reloads nutrient targets from storage; call after onboarding/settings writes. */
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
        // The currentDate change is observed in observeMacrosForCurrentDate(),
        // which restarts the daily-macros flow collector for the new date.
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
            // The daily-macros flow re-emits after the delete; we only need to
            // clear the selection here.
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
