package cz.krokviak.kalai.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalai.common.ImageStorage
import cz.krokviak.kalai.common.entities.FoodItemEntity
import cz.krokviak.kalai.common.repo.FoodRepository
import cz.krokviak.kalai.network.FoodAnalysisClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class FoodDetailViewModel(
    private val foodAnalysisClient: FoodAnalysisClient,
    private val foodRepository: FoodRepository,
    private val imageStorage: ImageStorage
) : ViewModel() {
    private val _uiState = MutableStateFlow(FoodDetailState())
    val uiState: StateFlow<FoodDetailState> = _uiState

    fun loadFood(foodId: Long) {
        viewModelScope.launch {
            val food = foodRepository.getFoodItem(foodId)
            _uiState.value = FoodDetailState(
                id = food?.id ?: 0,
                name = food?.name ?: "",
                calories = food?.calories ?: 0,
                protein = food?.protein ?: 0,
                fat = food?.fat ?: 0,
                carbs = food?.carbs ?: 0,
                portion = food?.portion ?: 0,
                healthScore = food?.healthScore ?: 0,
                localImagePath = food?.localImagePath,
                createdAt = food?.createdAt ?: Clock.System.now(),
                updatedAt = food?.updatedAt ?: Clock.System.now()
            )
        }
    }

    fun deleteFood() {
        viewModelScope.launch {
            foodRepository.deleteFoodItem(_uiState.value.id)
        }
    }

    fun increasePortion() {
        _uiState.update {
            it.copy(portion = it.portion + 1)
        }
    }

    fun decreasePortion() {
        if (_uiState.value.portion > 1) {
            _uiState.update {
                it.copy(portion = it.portion - 1)
            }
        }
    }

    fun onProteinChange(newValue: Int) {
        updateNutrients(
            protein = newValue,
            carbs = _uiState.value.carbs,
            fat = _uiState.value.fat
        )
    }

    fun onCarbsChange(newValue: Int) {
        updateNutrients(
            protein = _uiState.value.protein,
            carbs = newValue,
            fat = _uiState.value.fat
        )
    }

    fun onFatChange(newValue: Int) {
        updateNutrients(
            protein = _uiState.value.protein,
            carbs = _uiState.value.carbs,
            fat = newValue
        )
    }

    fun fixResult() {
        viewModelScope.launch {
            val bytes = _uiState.value.localImagePath?.let { imageStorage.getImageBytes(it) }
            if (bytes != null) {
                val analysisJob = viewModelScope.launch(Dispatchers.IO) {
                    val analysis = foodAnalysisClient.getAnalysis(bytes)
                    if (analysis != null) {
                        _uiState.update {
                            it.copy(
                                name = analysis.title ?: "Neznámé jídlo",
                                calories = (analysis.protein * 4) + (analysis.carbs * 4) + (analysis.fat * 9),
                                protein = analysis.protein,
                                fat = analysis.fat,
                                carbs = analysis.carbs,
                                healthScore = analysis.healthScore,
                            )
                        }
                    }
                }
                joinAll(analysisJob)
            }
        }
    }

    fun finish() {
        viewModelScope.launch {
            val updated = _uiState.value.copy(
                updatedAt = Clock.System.now()
            )
            foodRepository.updateFoodItem(
                FoodItemEntity(
                    id = _uiState.value.id,
                    name = updated.name,
                    calories = updated.calories,
                    protein = updated.protein,
                    fat = updated.fat,
                    carbs = updated.carbs,
                    portion = updated.portion,
                    healthScore = updated.healthScore,
                    createdAt = updated.createdAt,
                    updatedAt = updated.updatedAt,
                    localImagePath = updated.localImagePath ?: "",
                    loading = false
                )
            )
        }
    }

    private fun updateNutrients(protein: Int, carbs: Int, fat: Int) {
        val clampedProtein = protein.coerceIn(0, 500)
        val clampedCarbs = carbs.coerceIn(0, 500)
        val clampedFat = fat.coerceIn(0, 500)
        _uiState.update {
            it.copy(
                protein = clampedProtein,
                carbs = clampedCarbs,
                fat = clampedFat,
                calories = caloriesFromNutrients(
                    protein = clampedProtein,
                    carbs = clampedCarbs,
                    fat = clampedFat
                )
            )
        }
    }

    private fun caloriesFromNutrients(protein: Int, carbs: Int, fat: Int): Int {
        return (protein * 4) + (carbs * 4) + (fat * 9)
    }
}
