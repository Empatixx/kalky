package cz.krokviak.kalai.nutrientedit

import androidx.lifecycle.ViewModel
import cz.krokviak.kalai.detail.FoodDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class NutrientEditViewModel : ViewModel() {
    fun onProteinChange(it: Float) {
        _uiState.update {
            it.copy(protein = it.protein)
        }
    }

    fun onCarbsChange(it: Float) {
        _uiState.update {
            it.copy(carbs = it.carbs)
        }
    }

    fun onFatChange(it: Float) {
        _uiState.update {
            it.copy(fat = it.fat)
        }
    }

    fun onCalorieChange(it: Float) {
        _uiState.update {
            it.copy(calories = it.calories)
        }
    }

    private val _uiState = MutableStateFlow(NutrientEditState())
    val uiState: StateFlow<NutrientEditState> = _uiState

}