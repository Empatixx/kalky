package cz.krokviak.kalky.scenes.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalky.core.common.entities.PersonalInfoEntity
import cz.krokviak.kalky.core.common.repo.PersonalInfoRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val personalInfoRepo: PersonalInfoRepo
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    /** Reloads personal info from storage; call after onboarding writes. */
    fun refresh() {
        viewModelScope.launch {
            val info = personalInfoRepo.getLatestPersonalInfo()
            if (info != null) {
                _uiState.update {
                    it.copy(
                        weight = info.weightKg.toString(),
                        height = info.heightCm.toString(),
                        age = info.age.toString(),
                        gender = info.gender,
                        activityLevel = info.activityLevel
                    )
                }
            }
        }
    }

    fun onWeightChange(value: String) {
        _uiState.update { it.copy(weight = value, saved = false) }
    }

    fun onHeightChange(value: String) {
        _uiState.update { it.copy(height = value, saved = false) }
    }

    fun onAgeChange(value: String) {
        _uiState.update { it.copy(age = value, saved = false) }
    }

    fun onGenderChange(value: String) {
        _uiState.update { it.copy(gender = value, saved = false) }
    }

    fun onActivityLevelChange(value: Int) {
        _uiState.update { it.copy(activityLevel = value, saved = false) }
    }

    fun togglePickerField(field: ProfilePickerField) {
        _uiState.update {
            it.copy(activePickerField = if (it.activePickerField == field) null else field)
        }
    }

    fun save() {
        val state = _uiState.value
        val weight = state.weight.toFloatOrNull() ?: return
        val height = state.height.toFloatOrNull() ?: return
        val age = state.age.toIntOrNull() ?: return

        viewModelScope.launch {
            personalInfoRepo.insertPersonalInfo(
                PersonalInfoEntity(
                    weightKg = weight,
                    heightCm = height,
                    age = age,
                    gender = state.gender,
                    activityLevel = state.activityLevel
                )
            )
            _uiState.update { it.copy(saved = true) }
        }
    }
}
