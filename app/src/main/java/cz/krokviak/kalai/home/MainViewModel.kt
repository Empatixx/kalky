package cz.krokviak.kalai.home

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalai.camera.data.FoodAnalysisDto
import cz.krokviak.kalai.camera.entities.FoodItemEntity
import cz.krokviak.kalai.common.DatabaseProvider
import cz.krokviak.kalai.common.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.util.UUID

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState(
        dailyStats = generateFakeDailyStats(7) // e.g. start with a week by default
    ))
    val uiState: StateFlow<MainUiState> = _uiState

    fun onSceneSelected(scene: Scene) {
        _uiState.update {
            it.copy(currentScene = scene)
        }
    }
    fun onBottomNavItemSelected(index: Int) {
        _uiState.update { it.copy(selectedBottomNavItem = index) }
    }
    fun addFoodItemFromBytes(context: Application, imageBytes: ByteArray) {
        viewModelScope.launch {
            val foodRepository = FoodRepository(context)
            val imagePath = foodRepository.storeImageFile(imageBytes)

            val now = OffsetDateTime.now()
            val placeholder = FoodItemEntity(
                createdAt = now,
                updatedAt = now,
                localImagePath = imagePath,
                loading = true
            )

            // 1) Insert placeholder in DB
            val newId = foodRepository.insertFoodItem(placeholder)
            val insertedItem = placeholder.copy(id = newId)

            // 2) Immediately show it in UI (so user sees the “loading” card)
            _uiState.update { current ->
                current.copy(
                    recentlyAddedItems = listOf(insertedItem) + current.recentlyAddedItems,
                    loadingProgressForItems = current.loadingProgressForItems + (newId to 0) // progress=0
                )
            }
            recaculateMicros()

            // 3) Launch a “fake loading” animation for exactly 3s
            val animationJob = viewModelScope.launch {
                val startTime = System.currentTimeMillis()
                val duration = 6000L  // total 6seconds
                while (true) {
                    val elapsed = System.currentTimeMillis() - startTime
                    val fraction = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
                    val progress = (fraction * 100).toInt()

                    // Update the progress in UI state
                    _uiState.update { current ->
                        current.copy(
                            loadingProgressForItems = current.loadingProgressForItems + (newId to progress)
                        )
                    }

                    if (fraction >= 1f) break
                    delay(50) // repeat ~20 times over 3s
                }
            }

            // 4) In parallel, call the backend
            val analysisJob = viewModelScope.launch(Dispatchers.IO) {
                val analysis = analyzeImageBytes(imageBytes)
                if (analysis != null) {
                    // Build updated entity
                    val updated = insertedItem.copy(
                        name = analysis.title ?: "Neznámé jídlo",
                        calories = (analysis.protein * 4) + (analysis.carbs * 4) + (analysis.fat * 9),
                        protein = analysis.protein,
                        fat = analysis.fat,
                        carbs = analysis.carbs,
                        healthScore = analysis.healthScore,
                        loading = true, // STILL LOADING until animation is done
                        updatedAt = OffsetDateTime.now()
                    )
                    foodRepository.updateFoodItem(updated)

                    // Update in UI state
                    _uiState.update { current ->
                        current.copy(
                            recentlyAddedItems = current.recentlyAddedItems.map { existing ->
                                if (existing.id == updated.id) updated else existing
                            }
                        )
                    }
                }
            }

            // 5) Wait for *both* animation & analysis to finish
            joinAll(animationJob, analysisJob)

            // 6) Now that we have done 3s + analysis, mark item as “loaded = false” no longer
            val finalItem = _uiState.value.recentlyAddedItems
                .firstOrNull { it.id == newId }
                ?.copy(loading = false)
                ?: return@launch

            // Save to DB
            foodRepository.updateFoodItem(finalItem)

            // Remove from progress map & update the list
            _uiState.update { current ->
                current.copy(
                    loadingProgressForItems = current.loadingProgressForItems - newId,
                    recentlyAddedItems = current.recentlyAddedItems.map { existing ->
                        if (existing.id == newId) finalItem else existing
                    }
                )
            }

            // Recalculate macros after final
            recaculateMicros()
        }
    }

    /**
     * This is your "analysis logic" in the ViewModel.
     * The repository does NOT handle it, as you requested.
     */
    private suspend fun analyzeImageBytes(bytes: ByteArray): FoodAnalysisDto? {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = RequestBody.create(MediaType.parse("image/jpeg"), bytes)
                val response = RetrofitClient.instance.getAnalysis(requestBody).execute()
                if (response.isSuccessful) {
                    response.body()
                } else null
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to analyze image", e)
                null
            }
        }
    }


    fun recaculateMicros() {
        _uiState.update { current ->
            val totalCalories = current.recentlyAddedItems.sumBy { it.calories }
            val totalProtein = current.recentlyAddedItems.sumBy { it.protein }
            val totalFats = current.recentlyAddedItems.sumBy { it.fat }
            val totalCarbs = current.recentlyAddedItems.sumBy { it.carbs }

            current.copy(
                currentCalories = totalCalories,
                currentProtein = totalProtein,
                currentFats = totalFats,
                currentCarbs = totalCarbs
            )
        }
    }

    fun loadFoodItemsForDate(date: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateStr = date.format(DateTimeFormatter.ISO_DATE)
            val dao = DatabaseProvider.instance.foodItemDao()

            val itemsForDate = dao.getFoodItemsForDate(dateStr)

            val totalCalories = dao.getTotalCaloriesForDate(dateStr) ?: 0
            val totalFats = dao.getTotalFatsForDate(dateStr) ?: 0
            val totalCarbs = dao.getTotalCarbsForDate(dateStr) ?: 0
            val totalProtein = dao.getTotalProteinForDate(dateStr) ?: 0

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

    // For demonstration only, returns random stats
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


    fun setAnalyticsRange(range: AnalyticsRange) {
        _uiState.update { currentState ->
            currentState.copy(analyticsRange = range)
        }
    }

}
