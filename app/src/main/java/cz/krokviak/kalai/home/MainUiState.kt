package cz.krokviak.kalai.home

data class MainUiState(
    val selectedBottomNavItem: Int = 0,
    val caloriesLeft: Int = 1250,
    val proteinG: Int = 45,
    val carbsG: Int = 89,
    val fatG: Int = 48
)
