package cz.krokviak.kalai.camera.data

data class FoodAnalysisDto(
    val weight: Int,
    val foodType: String,
    val title: String,
    val protein: Int,
    val fat: Int,
    val carbs: Int,
    val healthScore: Int
){
    val calories: Int get() = protein * 4 + fat * 9 + carbs * 4
}

