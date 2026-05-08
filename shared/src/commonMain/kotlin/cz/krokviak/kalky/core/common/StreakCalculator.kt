package cz.krokviak.kalky.core.common

import cz.krokviak.kalky.core.common.repo.FoodRepository
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

private const val MAX_STREAK_DAYS = 366L

class StreakCalculator(private val foodRepository: FoodRepository) {
    suspend fun getCurrentStreak(): Int {
        // Only the most recent ~year of unique dates is needed — the streak by definition
        // ends at the first gap, and we never reward streaks longer than a year.
        val dates = foodRepository.getRecentDistinctFoodDates(MAX_STREAK_DAYS)
            .mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }
            .toSet()

        if (dates.isEmpty()) return 0

        val today = currentLocalDate()
        val yesterday = today.minus(1, DateTimeUnit.DAY)
        val startDate = if (today in dates) today
                        else if (yesterday in dates) yesterday
                        else return 0

        var count = 0
        var current = startDate
        while (current in dates) {
            count++
            current = current.minus(1, DateTimeUnit.DAY)
        }
        return count
    }
}
