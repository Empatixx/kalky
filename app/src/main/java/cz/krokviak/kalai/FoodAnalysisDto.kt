package cz.krokviak.kalai

data class FoodAnalysisDto(
    val type: String,
    val title: String,
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carbs: Int,
    val healthScore: Int
)
