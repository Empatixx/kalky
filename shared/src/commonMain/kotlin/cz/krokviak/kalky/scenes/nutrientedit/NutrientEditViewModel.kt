package cz.krokviak.kalky.scenes.nutrientedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalky.core.common.domain.GetLatestNutrientSettingsUseCase
import cz.krokviak.kalky.core.common.domain.UpdateNutrientSettingsUseCase
import cz.krokviak.kalky.core.common.entities.NutrientSettingEntity
import cz.krokviak.kalky.core.common.utils.caloriesFromMacros
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SAVE_DEBOUNCE_MS = 300L

class NutrientEditViewModel(
    private val getLatestSettings: GetLatestNutrientSettingsUseCase,
    private val updateSettings: UpdateNutrientSettingsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NutrientEditState())
    val uiState: StateFlow<NutrientEditState> = _uiState

    private var saveJob: Job? = null

    init {
        viewModelScope.launch {
            val latestSettings = getLatestSettings()
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

    fun toggleField(field: MacroField) {
        _uiState.update {
            it.copy(activeField = if (it.activeField == field) null else field)
        }
    }

    fun onProteinChange(newValue: Int) {
        _uiState.update {
            it.copy(
                protein = newValue,
                calories = caloriesFromNutrients(newValue, it.carbs, it.fat)
            )
        }
        scheduleSave()
    }

    fun onCarbsChange(newValue: Int) {
        _uiState.update {
            it.copy(
                carbs = newValue,
                calories = caloriesFromNutrients(it.protein, newValue, it.fat)
            )
        }
        scheduleSave()
    }

    fun onFatChange(newValue: Int) {
        _uiState.update {
            it.copy(
                fat = newValue,
                calories = caloriesFromNutrients(it.protein, it.carbs, newValue)
            )
        }
        scheduleSave()
    }

    private fun scheduleSave() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(SAVE_DEBOUNCE_MS)
            val state = _uiState.value
            updateSettings(
                NutrientSettingEntity(
                    targetProtein = state.protein,
                    targetCarbs = state.carbs,
                    targetFat = state.fat,
                    targetCalories = state.calories
                )
            )
        }
    }

    private fun caloriesFromNutrients(protein: Int, carbs: Int, fat: Int): Int =
        caloriesFromMacros(protein, carbs, fat)
}
