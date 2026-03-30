package cz.krokviak.kalky.common.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class PersonalInfoEntity(
    val id: Long = 0,
    val gender: String,
    val age: Int,
    val heightCm: Float,
    val weightKg: Float,
    val activityLevel: Int,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)
