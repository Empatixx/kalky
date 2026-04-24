package cz.krokviak.kalky.common.domain

import cz.krokviak.kalky.common.StreakCalculator

class GetStreakUseCase(
    private val streakCalculator: StreakCalculator,
) {
    suspend operator fun invoke(): Int = streakCalculator.getCurrentStreak()
}
