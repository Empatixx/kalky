package cz.krokviak.kalky.core.camera.data

import cz.krokviak.kalky.core.common.utils.caloriesFromMacros
import kotlinx.serialization.Serializable

@Serializable
data class FoodAnalysisDto(
    val weight: Int = 0,
    val foodType: String = "",
    val title: String = "",
    val protein: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0,
    val healthScore: Int = 0
) {
    val calories: Int get() = caloriesFromMacros(protein, carbs, fat)
}
