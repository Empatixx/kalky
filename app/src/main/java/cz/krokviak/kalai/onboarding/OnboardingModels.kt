package cz.krokviak.kalai.onboarding

enum class OnboardingStep(val title: String) {
    GENDER("Vyber pohlaví"),
    WEIGHT("Kolik vážíš?"),
    HEIGHT("Jak jsi vysoký/á?"),
    AGE("Kolik je ti let?"),
    ACTIVITY("Jak aktivní jsi?"),
    GOAL("Jaký máš cíl?"),
    PROMO("Promo kód")
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
    val goal: GoalChoice,
    val promoCode: String
)
