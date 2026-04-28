package cz.krokviak.kalky.settings

import androidx.compose.runtime.Immutable

enum class ProfilePickerField { WEIGHT, HEIGHT, AGE }

@Immutable
data class SettingsUiState(
    val weight: String = "",
    val height: String = "",
    val age: String = "",
    val gender: String = "Muž",
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
