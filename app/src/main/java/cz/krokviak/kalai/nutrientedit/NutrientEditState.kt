package cz.krokviak.kalai.nutrientedit

data class NutrientEditState(
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val calories: Float = 0f
)