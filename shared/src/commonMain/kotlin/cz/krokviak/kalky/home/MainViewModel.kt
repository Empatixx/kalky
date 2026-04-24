package cz.krokviak.kalky.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalky.common.FoodPhotoAnalyzer
import cz.krokviak.kalky.common.StreakCalculator
import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.common.repo.FoodRepository
import cz.krokviak.kalky.common.repo.NutrientSettingRepo
import cz.krokviak.kalky.db.DatabaseSeeder
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
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
    private val foodRepository: FoodRepository,
    private val nutrientSettingRepo: NutrientSettingRepo,
    private val foodPhotoAnalyzer: FoodPhotoAnalyzer,
    private val streakCalculator: StreakCalculator,
    private val databaseSeeder: DatabaseSeeder,
    private val seedMockData: Boolean = false
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState(
        dailyStats = generateFakeDailyStats(7)
    ))
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        viewModelScope.launch {
            if (seedMockData) {
                withContext(Dispatchers.IO) { databaseSeeder.seedIfEmpty() }
            }
            val latestSettings = nutrientSettingRepo.getLatestNutrientSettings()
            _uiState.update {
                it.copy(
                    maxProtein = latestSettings?.targetProtein ?: 0,
                    maxCarbs = latestSettings?.targetCarbs ?: 0,
                    maxFats = latestSettings?.targetFat ?: 0,
                    maxCalories = latestSettings?.targetCalories ?: 0
                )
            }
            refreshStreak()
        }
    }

    private suspend fun refreshStreak() {
        val streak = streakCalculator.getCurrentStreak()
        _uiState.update { it.copy(currentStreak = streak) }
    }

    fun addFoodItemFromBytes(imageBytes: ByteArray) {
        foodPhotoAnalyzer.analyze(
            scope = viewModelScope,
            imageBytes = imageBytes,
            onPlaceholderInserted = { placeholder ->
                _uiState.update { current ->
                    current.copy(
                        recentlyAddedItems = current.recentlyAddedItems.mutate { it.add(0, placeholder) },
                        loadingItems = current.loadingItems.add(placeholder.id)
                    )
                }
                recalculateMacros()
            },
            onAnalysisComplete = { analyzed ->
                _uiState.update { current ->
                    current.copy(
                        recentlyAddedItems = current.recentlyAddedItems.replaceById(analyzed.id, analyzed)
                    )
                }
            },
            onFinalCommitted = { finalItem ->
                _uiState.update { current ->
                    current.copy(
                        loadingItems = current.loadingItems.remove(finalItem.id),
                        recentlyAddedItems = current.recentlyAddedItems.replaceById(finalItem.id, finalItem)
                    )
                }
                recalculateMacros()
            }
        )
    }

    fun addFoodItemFromBarcode(
        name: String,
        calories: Int,
        protein: Int,
        fat: Int,
        carbs: Int
    ) {
        viewModelScope.launch {
            val now = Clock.System.now()
            val item = FoodItemEntity(
                name = name,
                calories = calories,
                protein = protein,
                fat = fat,
                carbs = carbs,
                healthScore = 0,
                createdAt = now,
                updatedAt = now,
                localImagePath = "",
                loading = false
            )

            val newId = foodRepository.insertFoodItem(item)
            val insertedItem = item.copy(id = newId)

            _uiState.update { current ->
                current.copy(
                    recentlyAddedItems = current.recentlyAddedItems.mutate { it.add(0, insertedItem) }
                )
            }
            recalculateMacros()
        }
    }

    fun recalculateMacros() {
        _uiState.update { current ->
            val totalCalories = current.recentlyAddedItems.sumOf { it.calories }
            val totalProtein = current.recentlyAddedItems.sumOf { it.protein }
            val totalFats = current.recentlyAddedItems.sumOf { it.fat }
            val totalCarbs = current.recentlyAddedItems.sumOf { it.carbs }

            current.copy(
                currentCalories = totalCalories,
                currentProtein = totalProtein,
                currentFats = totalFats,
                currentCarbs = totalCarbs
            )
        }
        viewModelScope.launch { refreshStreak() }
    }

    fun loadFoodItemsForDate(date: LocalDate) {
        viewModelScope.launch {
            val dateStr = date.toString()

            val itemsForDate = foodRepository.getFoodItemsForDate(dateStr)
            val totalCalories = foodRepository.getTotalCaloriesForDate(dateStr)
            val totalFats = foodRepository.getTotalFatsForDate(dateStr)
            val totalCarbs = foodRepository.getTotalCarbsForDate(dateStr)
            val totalProtein = foodRepository.getTotalProteinForDate(dateStr)

            _uiState.update { current ->
                current.copy(
                    recentlyAddedItems = itemsForDate.toPersistentList(),
                    currentCalories = totalCalories,
                    currentFats = totalFats,
                    currentCarbs = totalCarbs,
                    currentProtein = totalProtein
                )
            }
        }
    }

    private fun generateFakeDailyStats(days: Int): kotlinx.collections.immutable.PersistentList<DailyStats> {
        val labels = listOf("Po", "Út", "St", "Čt", "Pá", "So", "Ne")
        return (0 until days).map { i ->
            val label = if (days == 7) labels[i % 7] else "Den ${i + 1}"
            DailyStats(
                dayLabel = label,
                protein = (20..100).random(),
                carbs = (50..200).random(),
                fat = (10..80).random()
            )
        }.toPersistentList()
    }

    fun resetToToday() {
        onDateSelected(cz.krokviak.kalky.common.currentLocalDate())
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { current ->
            current.copy(currentDate = date)
        }
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
            for (id in ids) {
                foodRepository.deleteFoodItem(id)
            }
            _uiState.update { current ->
                current.copy(
                    recentlyAddedItems = current.recentlyAddedItems
                        .mutate { list -> list.removeAll { it.id in ids } },
                    selectedFoodIds = persistentSetOf()
                )
            }
            recalculateMacros()
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
                maxCalories = calories
            )
        }
    }
}

private fun kotlinx.collections.immutable.PersistentList<FoodItemEntity>.replaceById(
    id: Long,
    replacement: FoodItemEntity
): kotlinx.collections.immutable.PersistentList<FoodItemEntity> {
    val idx = indexOfFirst { it.id == id }
    return if (idx < 0) this else mutate { it[idx] = replacement }
}
