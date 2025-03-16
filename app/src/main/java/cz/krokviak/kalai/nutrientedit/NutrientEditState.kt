package cz.krokviak.kalai.nutrientedit

data class NutrientEditState(
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val calories: Int = 0
)