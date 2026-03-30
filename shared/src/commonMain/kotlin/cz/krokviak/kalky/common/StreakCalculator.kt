package cz.krokviak.kalky.common

import cz.krokviak.kalky.common.repo.FoodRepository
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

class StreakCalculator(private val foodRepository: FoodRepository) {
    suspend fun getCurrentStreak(): Int {
        val dates = foodRepository.getDistinctFoodDates()
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
