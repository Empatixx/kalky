package cz.krokviak.kalky.common.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class FoodItemEntity(
    val id: Long = 0,
    val name: String = "Neznámé jídlo",
    val calories: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0,
    val portion: Int = 1,
    val healthScore: Int = 0,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
    val localImagePath: String = "",
    val loading: Boolean = true,
    val isCustom: Boolean = false
)
