package cz.krokviak.kalky.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalky.common.ImageStorage
import cz.krokviak.kalky.common.domain.DeleteFoodItemUseCase
import cz.krokviak.kalky.common.domain.GetFoodItemUseCase
import cz.krokviak.kalky.common.domain.UpdateFoodItemUseCase
import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.common.error.UiError
import cz.krokviak.kalky.common.utils.caloriesFromMacros
import cz.krokviak.kalky.network.FoodAnalysisClient
import cz.krokviak.kalky.nutrientedit.MacroField
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
    private val getFoodItem: GetFoodItemUseCase,
    private val updateFoodItem: UpdateFoodItemUseCase,
    private val deleteFoodItem: DeleteFoodItemUseCase,
    private val imageStorage: ImageStorage,
    private val clock: Clock,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FoodDetailState())
    val uiState: StateFlow<FoodDetailState> = _uiState

    fun loadFood(foodId: Long) {
        viewModelScope.launch {
            val food = getFoodItem(foodId)
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
                createdAt = food?.createdAt ?: clock.now(),
                updatedAt = food?.updatedAt ?: clock.now()
            )
        }
    }

    fun toggleField(field: MacroField) {
        _uiState.update {
            it.copy(activeField = if (it.activeField == field) null else field)
        }
    }

    fun deleteFood() {
        viewModelScope.launch {
            deleteFoodItem(_uiState.value.id)
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
            if (bytes == null) {
                _uiState.update { it.copy(error = UiError.PhotoAnalysis) }
                return@launch
            }
            val analysisJob = viewModelScope.launch(Dispatchers.IO) {
                val analysis = foodAnalysisClient.getAnalysis(bytes)
                if (analysis != null) {
                    _uiState.update {
                        it.copy(
                            name = analysis.title ?: "",
                            calories = caloriesFromMacros(analysis.protein, analysis.carbs, analysis.fat),
                            protein = analysis.protein,
                            fat = analysis.fat,
                            carbs = analysis.carbs,
                            healthScore = analysis.healthScore,
                        )
                    }
                } else {
                    _uiState.update { it.copy(error = UiError.PhotoAnalysis) }
                }
            }
            joinAll(analysisJob)
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun finish() {
        viewModelScope.launch {
            val updated = _uiState.value.copy(
                updatedAt = clock.now()
            )
            updateFoodItem(
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

    private fun caloriesFromNutrients(protein: Int, carbs: Int, fat: Int): Int =
        caloriesFromMacros(protein, carbs, fat)
}
