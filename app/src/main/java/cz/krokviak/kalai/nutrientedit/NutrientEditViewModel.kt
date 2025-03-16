package cz.krokviak.kalai.nutrientedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalai.detail.FoodDetailState
import cz.krokviak.kalai.home.repo.NutrientSettingRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NutrientEditViewModel : ViewModel() {
    private val repo = NutrientSettingRepo()

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