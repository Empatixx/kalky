package cz.krokviak.kalai.onboarding

enum class OnboardingStepRoute(val route: String, val title: String) {
    GENDER("onboarding_gender", "Vyber pohlaví"),
    WEIGHT("onboarding_weight", "Kolik vážíš?"),
    HEIGHT("onboarding_height", "Jak jsi vysoký/á?"),
    AGE("onboarding_age", "Kolik je ti let?"),
    ACTIVITY("onboarding_activity", "Jak aktivní jsi?"),
    GOAL("onboarding_goal", "Jaký máš cíl?")
}

enum class GoalChoice(val label: String) {
    LOSE("Chci zhubnout"),
    MAINTAIN("Chci udržet"),
    GAIN("Chci nabrat")
}

data class OnboardingResult(
    val gender: String,
    val weight: String,
    val height: String,
    val age: String,
    val activityLevel: Int,
    val goal: GoalChoice
)
