package cz.krokviak.kalai.common

import kotlinx.serialization.Serializable

@Serializable
object DefaultRoute

@Serializable
data class FoodDetailRoute(
    val id: Long,
)

@Serializable
object NutrientEditRoute

