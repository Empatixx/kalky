package cz.krokviak.kalai.common

import kotlinx.serialization.Serializable

@Serializable
object DefaultRoute

@Serializable
object OnboardingRoute

@Serializable
data class FoodDetailRoute(
    val id: Long,
)

@Serializable
object NutrientEditRoute

@Serializable
object CustomFoodRoute

@Serializable
object ManualFoodEntryRoute

@Serializable
object TermsRoute

@Serializable
object PrivacyPolicyRoute
