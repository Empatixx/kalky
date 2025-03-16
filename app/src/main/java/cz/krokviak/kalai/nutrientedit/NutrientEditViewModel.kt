package cz.krokviak.kalai.nutrientedit

import androidx.lifecycle.ViewModel
import cz.krokviak.kalai.detail.FoodDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class NutrientEditViewModel : ViewModel() {
    fun onProteinChange(newValue: Int) {
        _uiState.update {
            it.copy(protein = newValue)
        }
    }

    fun onCarbsChange(newValue: Int) {
        _uiState.update {
            it.copy(carbs = newValue)
        }
    }

    fun onFatChange(newValue: Int) {
        _uiState.update {
            it.copy(fat = newValue)
        }
    }

    fun onCalorieChange(newValue: Int) {
        _uiState.update {
            it.copy(calories = newValue)
        }
    }

    private val _uiState = MutableStateFlow(NutrientEditState())
    val uiState: StateFlow<NutrientEditState> = _uiState

}