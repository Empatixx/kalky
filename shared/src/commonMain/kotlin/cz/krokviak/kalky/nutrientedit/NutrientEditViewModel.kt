package cz.krokviak.kalky.nutrientedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalky.common.entities.NutrientSettingEntity
import cz.krokviak.kalky.common.repo.NutrientSettingRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NutrientEditViewModel(
    private val nutrientSettingRepo: NutrientSettingRepo
) : ViewModel() {
    private val _uiState = MutableStateFlow(NutrientEditState())
    val uiState: StateFlow<NutrientEditState> = _uiState

    init {
        viewModelScope.launch {
            val latestSettings = nutrientSettingRepo.getLatestNutrientSettings()
            _uiState.update {
                it.copy(
                    protein = latestSettings?.targetProtein ?: 0,
                    carbs = latestSettings?.targetCarbs ?: 0,
                    fat = latestSettings?.targetFat ?: 0,
                    calories = latestSettings?.targetCalories ?: 0
                )
            }
        }
    }

    fun onProteinChange(newValue: Int) {
        _uiState.update {
            it.copy(protein = newValue, calories = caloriesFromNutrients(newValue, uiState.value.carbs, uiState.value.fat))
        }
        viewModelScope.launch {
            nutrientSettingRepo.insertNutrientSettings(
                NutrientSettingEntity(
                    targetProtein = newValue,
                    targetCarbs = uiState.value.carbs,
                    targetFat = uiState.value.fat,
                    targetCalories = caloriesFromNutrients(newValue, uiState.value.carbs, uiState.value.fat)
                )
            )
        }
    }

    fun onCarbsChange(newValue: Int) {
        _uiState.update {
            it.copy(carbs = newValue, calories = caloriesFromNutrients(uiState.value.protein, newValue, uiState.value.fat))
        }
        viewModelScope.launch {
            nutrientSettingRepo.insertNutrientSettings(
                NutrientSettingEntity(
                    targetProtein = uiState.value.protein,
                    targetCarbs = newValue,
                    targetFat = uiState.value.fat,
                    targetCalories = caloriesFromNutrients(uiState.value.protein, newValue, uiState.value.fat)
                )
            )
        }
    }

    fun onFatChange(newValue: Int) {
        _uiState.update {
            it.copy(fat = newValue, calories = caloriesFromNutrients(uiState.value.protein, uiState.value.carbs, newValue))
        }
        viewModelScope.launch {
            nutrientSettingRepo.insertNutrientSettings(
                NutrientSettingEntity(
                    targetProtein = uiState.value.protein,
                    targetCarbs = uiState.value.carbs,
                    targetFat = newValue,
                    targetCalories = caloriesFromNutrients(uiState.value.protein, uiState.value.carbs, newValue)
                )
            )
        }
    }

    private fun caloriesFromNutrients(protein: Int, carbs: Int, fat: Int): Int {
        return (protein * 4) + (carbs * 4) + (fat * 9)
    }
}
