package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.StreakCalculator

open class GetStreakUseCase(
    private val streakCalculator: StreakCalculator,
) {
    open suspend operator fun invoke(): Int = streakCalculator.getCurrentStreak()
}
