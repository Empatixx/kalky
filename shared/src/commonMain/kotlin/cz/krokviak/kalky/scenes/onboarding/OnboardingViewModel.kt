package cz.krokviak.kalky.scenes.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.Immutable
import cz.krokviak.kalky.core.common.repo.PersonalInfoRepo
import cz.krokviak.kalky.core.common.utils.caloriesFromMacros
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import cz.krokviak.kalky.core.common.formatFloat1
import kotlin.math.roundToInt

@Immutable
data class OnboardingUiState(
    val gender: String = "Mu\u017E",
    val activityLevel: Int = 2,
    val goalChoice: GoalChoice = GoalChoice.MAINTAIN,
    val weightIndex: Int = DEFAULT_WEIGHT_INDEX,
    val heightIndex: Int = DEFAULT_HEIGHT_INDEX,
    val ageIndex: Int = DEFAULT_AGE_INDEX,
    val targetCalories: Int = 0,
    val targetProtein: Int = 0,
    val targetCarbs: Int = 0,
    val targetFat: Int = 0,
    val promoCode: String = ""
)

class OnboardingViewModel(
    private val personalInfoRepo: PersonalInfoRepo
) : ViewModel() {
    val weightValues: List<String> = (300..2500).map { index -> formatFloat1(index / 10f) }
    val heightValues: List<String> = (100..250).map { it.toString() }
    val ageValues: List<String> = (1..120).map { it.toString() }

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _completed = MutableSharedFlow<OnboardingResult>(extraBufferCapacity = 1)
    val completed: SharedFlow<OnboardingResult> = _completed.asSharedFlow()

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

    fun onProteinChanged(value: Int) {
        _uiState.update {
            it.copy(targetProtein = value, targetCalories = caloriesFromMacros(value, it.targetCarbs, it.targetFat))
        }
    }

    fun onCarbsChanged(value: Int) {
        _uiState.update {
            it.copy(targetCarbs = value, targetCalories = caloriesFromMacros(it.targetProtein, value, it.targetFat))
        }
    }

    fun onFatChanged(value: Int) {
        _uiState.update {
            it.copy(targetFat = value, targetCalories = caloriesFromMacros(it.targetProtein, it.targetCarbs, value))
        }
    }

    fun calculateMacros() {
        val state = _uiState.value
        val weightKg = weightValues[state.weightIndex].toFloat()
        val heightCm = heightValues[state.heightIndex].toFloat()
        val age = ageValues[state.ageIndex].toInt()

        // Mifflin-St Jeor
        val bmr = if (state.gender == "Mu\u017E") {
            10.0 * weightKg + 6.25 * heightCm - 5.0 * age + 5
        } else {
            10.0 * weightKg + 6.25 * heightCm - 5.0 * age - 161
        }

        val activityMultiplier = when (state.activityLevel) {
            1 -> 1.2
            2 -> 1.375
            3 -> 1.55
            4 -> 1.725
            else -> 1.375
        }

        val tdee = bmr * activityMultiplier

        val targetCalories = when (state.goalChoice) {
            GoalChoice.LOSE -> (tdee - 500).roundToInt()
            GoalChoice.MAINTAIN -> tdee.roundToInt()
            GoalChoice.GAIN -> (tdee + 300).roundToInt()
        }

        val protein = (targetCalories * 0.30 / 4).roundToInt()
        val carbs = (targetCalories * 0.40 / 4).roundToInt()
        val fat = (targetCalories * 0.30 / 9).roundToInt()

        _uiState.update {
            it.copy(
                targetCalories = targetCalories.coerceAtLeast(0),
                targetProtein = protein.coerceAtLeast(0),
                targetCarbs = carbs.coerceAtLeast(0),
                targetFat = fat.coerceAtLeast(0)
            )
        }
    }

    fun submit() {
        val result = buildResult()
        viewModelScope.launch { _completed.emit(result) }
    }

    private fun buildResult(): OnboardingResult {
        val state = _uiState.value
        return OnboardingResult(
            gender = state.gender,
            weight = weightValues[state.weightIndex],
            height = heightValues[state.heightIndex],
            age = ageValues[state.ageIndex],
            activityLevel = state.activityLevel,
            goal = state.goalChoice,
            targetCalories = state.targetCalories,
            targetProtein = state.targetProtein,
            targetCarbs = state.targetCarbs,
            targetFat = state.targetFat,
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
