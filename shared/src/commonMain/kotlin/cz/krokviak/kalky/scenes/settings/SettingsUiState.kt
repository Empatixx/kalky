package cz.krokviak.kalky.scenes.settings

import androidx.compose.runtime.Immutable
import cz.krokviak.kalky.core.common.entities.Gender

enum class ProfilePickerField { WEIGHT, HEIGHT, AGE }

@Immutable
data class SettingsUiState(
    val weight: String = "",
    val height: String = "",
    val age: String = "",
    val gender: Gender = Gender.MALE,
    val activityLevel: Int = 2,
    val saved: Boolean = false,
    val activePickerField: ProfilePickerField? = null,
) {
    val bmi: Float?
        get() {
            val w = weight.toFloatOrNull() ?: return null
            val h = height.toFloatOrNull() ?: return null
            if (h <= 0f) return null
            val hMeters = h / 100f
            return w / (hMeters * hMeters)
        }
}
