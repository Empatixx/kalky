package cz.krokviak.kalky.scenes.nutrientedit

import androidx.compose.runtime.Immutable

enum class MacroField { PROTEIN, CARBS, FAT }

@Immutable
data class NutrientEditState(
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val calories: Int = 0,
    val activeField: MacroField? = null,
)
