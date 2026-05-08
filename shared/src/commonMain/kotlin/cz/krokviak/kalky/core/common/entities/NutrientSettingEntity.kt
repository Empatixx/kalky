package cz.krokviak.kalky.core.common.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class NutrientSettingEntity(
    val id: Long = 0,
    val targetCalories: Int = 0,
    val targetProtein: Int = 0,
    val targetFat: Int = 0,
    val targetCarbs: Int = 0,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)
