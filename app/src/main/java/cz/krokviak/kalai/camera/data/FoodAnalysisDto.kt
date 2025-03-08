package cz.krokviak.kalai.camera.data

data class FoodAnalysisDto(
    val foodType: String,
    val title: String,
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carbs: Int,
    val healthScore: Int
)
