package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.StreakCalculator

class GetStreakUseCase(
    private val streakCalculator: StreakCalculator,
) {
    suspend operator fun invoke(): Int = streakCalculator.getCurrentStreak()
}
