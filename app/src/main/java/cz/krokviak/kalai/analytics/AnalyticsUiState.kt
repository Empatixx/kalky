package cz.krokviak.kalai.analytics

data class AnalyticsUiState(
    val weights: List<Double> = emptyList(),
    val caloriesBars: List<CaloriesBar> = emptyList(),
    val selectedRange: AnalyticsRange = AnalyticsRange.WEEK
) {
}

public data class CaloriesBar(
    val label: String,
    val protein: Int,
    val carbs: Int,
    val fat: Int
){
    val totalCalories: Int get() = protein * 4 + carbs * 4 + fat * 9
}
