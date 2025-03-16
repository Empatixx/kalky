package cz.krokviak.kalai.nutrientedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalai.camera.entities.NutrientSettingEntity
import cz.krokviak.kalai.detail.FoodDetailState
import cz.krokviak.kalai.home.repo.NutrientSettingRepo
import kotlinx.coroutines.Dispatchers
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
            val latestSettings = withContext(Dispatchers.IO) {
                nutrientSettingRepo.getLatestNutrientSettings() // Now runs on a background thread
            }
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
             it.copy(protein = newValue)
         }
         viewModelScope.launch {

             withContext(Dispatchers.IO) {
                 nutrientSettingRepo.insertNutrientSettings(
                     NutrientSettingEntity(
                         targetProtein = newValue,
                         targetCarbs = uiState.value.carbs,
                         targetFat = uiState.value.fat,
                         targetCalories = uiState.value.calories
                     )
                 )
             }
         }
     }

    fun onCarbsChange(newValue: Int) {
        _uiState.update {
            it.copy(carbs = newValue)
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                nutrientSettingRepo.insertNutrientSettings(
                    NutrientSettingEntity(
                        targetProtein = uiState.value.protein,
                        targetCarbs = newValue,
                        targetFat = uiState.value.fat,
                        targetCalories = uiState.value.calories
                    )
                )
            }
        }
    }

    fun onFatChange(newValue: Int) {
        _uiState.update {
            it.copy(fat = newValue)
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                nutrientSettingRepo.insertNutrientSettings(
                    NutrientSettingEntity(
                        targetProtein = uiState.value.protein,
                        targetCarbs = uiState.value.carbs,
                        targetFat = newValue,
                        targetCalories = uiState.value.calories
                    )
                )
            }
        }
    }

    fun onCalorieChange(newValue: Int) {
        _uiState.update {
            it.copy(calories = newValue)
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                nutrientSettingRepo.insertNutrientSettings(
                    NutrientSettingEntity(
                        targetProtein = uiState.value.protein,
                        targetCarbs = uiState.value.carbs,
                        targetFat = uiState.value.fat,
                        targetCalories = newValue
                    )
                )
            }
        }
    }

}