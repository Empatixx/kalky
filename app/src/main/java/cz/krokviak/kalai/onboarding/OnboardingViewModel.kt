package cz.krokviak.kalai.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalai.common.repo.PersonalInfoRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

data class OnboardingUiState(
    val gender: String = "Muž",
    val activityLevel: Int = 2,
    val goalChoice: GoalChoice = GoalChoice.MAINTAIN,
    val weightIndex: Int = DEFAULT_WEIGHT_INDEX,
    val heightIndex: Int = DEFAULT_HEIGHT_INDEX,
    val ageIndex: Int = DEFAULT_AGE_INDEX,
    val promoCode: String = ""
)

class OnboardingViewModel(
    private val personalInfoRepo: PersonalInfoRepo
) : ViewModel() {
    val weightValues: List<String> = (300..2500).map { index -> String.format(Locale.US, "%.1f", index / 10f) }
    val heightValues: List<String> = (100..250).map { it.toString() }
    val ageValues: List<String> = (1..120).map { it.toString() }

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val latestInfo = personalInfoRepo.getLatestPersonalInfo() ?: return@launch
            _uiState.update {
                it.copy(
                    gender = latestInfo.gender.ifBlank { "Muž" },
                    activityLevel = latestInfo.activityLevel.coerceIn(1, 4),
                    weightIndex = resolveWeightIndex(latestInfo.weightKg),
                    heightIndex = resolveIndex(latestInfo.heightCm.roundToInt(), 100, 250),
                    ageIndex = resolveIndex(latestInfo.age, 1, 120)
                )
            }
        }
    }

    fun onGenderSelected(value: String) {
        _uiState.update { it.copy(gender = value) }
    }

    fun onActivityLevelSelected(value: Int) {
        _uiState.update { it.copy(activityLevel = value.coerceIn(1, 4)) }
    }

    fun onGoalSelected(value: GoalChoice) {
        _uiState.update { it.copy(goalChoice = value) }
    }

    fun onWeightIndexChanged(index: Int) {
        _uiState.update { it.copy(weightIndex = index.coerceIn(0, weightValues.lastIndex)) }
    }

    fun onHeightIndexChanged(index: Int) {
        _uiState.update { it.copy(heightIndex = index.coerceIn(0, heightValues.lastIndex)) }
    }

    fun onAgeIndexChanged(index: Int) {
        _uiState.update { it.copy(ageIndex = index.coerceIn(0, ageValues.lastIndex)) }
    }

    fun onPromoCodeChange(value: String) {
        _uiState.update { it.copy(promoCode = value) }
    }

    fun buildResult(): OnboardingResult {
        val state = _uiState.value
        return OnboardingResult(
            gender = state.gender,
            weight = weightValues[state.weightIndex],
            height = heightValues[state.heightIndex],
            age = ageValues[state.ageIndex],
            activityLevel = state.activityLevel,
            goal = state.goalChoice,
            promoCode = state.promoCode.trim()
        )
    }
}

private const val DEFAULT_WEIGHT_INDEX = 500 // 80.0 kg
private const val DEFAULT_HEIGHT_INDEX = 70 // 170 cm
private const val DEFAULT_AGE_INDEX = 24 // 25 years

private fun resolveWeightIndex(value: Float): Int {
    return ((value * 10f).roundToInt() - 300).coerceIn(0, 2200)
}

private fun resolveIndex(value: Int, minValue: Int, maxValue: Int): Int {
    return (value - minValue).coerceIn(0, maxValue - minValue)
}
