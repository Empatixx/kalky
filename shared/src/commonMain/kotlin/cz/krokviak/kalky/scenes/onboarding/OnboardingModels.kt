package cz.krokviak.kalky.scenes.onboarding

enum class OnboardingStep {
    LANGUAGE, UNITS, APPEARANCE, GENDER, WEIGHT, HEIGHT, AGE, ACTIVITY, GOAL, MACROS, PROMO
}

enum class GoalChoice {
    LOSE, MAINTAIN, GAIN
}

data class OnboardingResult(
    val gender: String,
    val weight: String,
    val height: String,
    val age: String,
    val activityLevel: Int,
    val goal: GoalChoice,
    val targetCalories: Int,
    val targetProtein: Int,
    val targetCarbs: Int,
    val targetFat: Int,
    val promoCode: String
)
