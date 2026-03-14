package cz.krokviak.kalai.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalai.common.ImageStorage
import cz.krokviak.kalai.common.entities.FoodItemEntity
import cz.krokviak.kalai.common.repo.FoodRepository
import cz.krokviak.kalai.common.repo.NutrientSettingRepo
import cz.krokviak.kalai.network.FoodAnalysisClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

class MainViewModel(
    private val foodRepository: FoodRepository,
    private val nutrientSettingRepo: NutrientSettingRepo,
    private val foodAnalysisClient: FoodAnalysisClient,
    private val imageStorage: ImageStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState(
        dailyStats = generateFakeDailyStats(7)
    ))
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        viewModelScope.launch {
            val latestSettings = nutrientSettingRepo.getLatestNutrientSettings()
            _uiState.update {
                it.copy(
                    maxProtein = latestSettings?.targetProtein ?: 0,
                    maxCarbs = latestSettings?.targetCarbs ?: 0,
                    maxFats = latestSettings?.targetFat ?: 0,
                    maxCalories = latestSettings?.targetCalories ?: 0
                )
            }
        }
    }

    fun addFoodItemFromBytes(imageBytes: ByteArray) {
        viewModelScope.launch {
            val imagePath = imageStorage.storeImageFile(imageBytes)

            val now = Clock.System.now()
            val placeholder = FoodItemEntity(
                createdAt = now,
                updatedAt = now,
                localImagePath = imagePath,
                loading = true
            )

            val newId = foodRepository.insertFoodItem(placeholder)
            val insertedItem = placeholder.copy(id = newId)

            _uiState.update { current ->
                current.copy(
                    recentlyAddedItems = listOf(insertedItem) + current.recentlyAddedItems,
                    loadingProgressForItems = current.loadingProgressForItems + (newId to 0)
                )
            }
            recalculateMacros()

            val animationJob = viewModelScope.launch {
                val startTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
                val duration = 6000L
                while (true) {
                    val elapsed = kotlinx.datetime.Clock.System.now().toEpochMilliseconds() - startTime
                    val fraction = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
                    val progress = (fraction * 100).toInt()

                    _uiState.update { current ->
                        current.copy(
                            loadingProgressForItems = current.loadingProgressForItems + (newId to progress)
                        )
                    }

                    if (fraction >= 1f) break
                    delay(50)
                }
            }

            val analysisJob = viewModelScope.launch(Dispatchers.IO) {
                val analysis = foodAnalysisClient.getAnalysis(imageBytes)
                if (analysis != null) {
                    val updated = insertedItem.copy(
                        name = analysis.title ?: "Neznámé jídlo",
                        calories = (analysis.protein * 4) + (analysis.carbs * 4) + (analysis.fat * 9),
                        protein = analysis.protein,
                        fat = analysis.fat,
                        carbs = analysis.carbs,
                        healthScore = analysis.healthScore,
                        loading = true,
                        updatedAt = Clock.System.now()
                    )
                    foodRepository.updateFoodItem(updated)

                    _uiState.update { current ->
                        current.copy(
                            recentlyAddedItems = current.recentlyAddedItems.map { existing ->
                                if (existing.id == updated.id) updated else existing
                            }
                        )
                    }
                }
            }

            joinAll(animationJob, analysisJob)

            val finalItem = _uiState.value.recentlyAddedItems
                .firstOrNull { it.id == newId }
                ?.copy(loading = false)
                ?: return@launch

            foodRepository.updateFoodItem(finalItem)

            _uiState.update { current ->
                current.copy(
                    loadingProgressForItems = current.loadingProgressForItems - newId,
                    recentlyAddedItems = current.recentlyAddedItems.map { existing ->
                        if (existing.id == newId) finalItem else existing
                    }
                )
            }

            recalculateMacros()
        }
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
                    recentlyAddedItems = listOf(insertedItem) + current.recentlyAddedItems
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
                    recentlyAddedItems = itemsForDate,
                    currentCalories = totalCalories,
                    currentFats = totalFats,
                    currentCarbs = totalCarbs,
                    currentProtein = totalProtein
                )
            }
        }
    }

    private fun generateFakeDailyStats(days: Int): List<DailyStats> {
        val labels = listOf("Po", "Út", "St", "Čt", "Pá", "So", "Ne")
        return (0 until days).map { i ->
            val label = if (days == 7) labels[i % 7] else "Den ${i+1}"
            DailyStats(
                dayLabel = label,
                protein = (20..100).random(),
                carbs = (50..200).random(),
                fat = (10..80).random()
            )
        }
    }

    fun resetToToday() {
        onDateSelected(cz.krokviak.kalai.common.currentLocalDate())
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { current ->
            current.copy(currentDate = date)
        }
        loadFoodItemsForDate(date)
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
