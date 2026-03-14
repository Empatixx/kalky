package cz.krokviak.kalai.notifications

import cz.krokviak.kalai.common.repo.FoodRepository
import cz.krokviak.kalai.common.repo.NutrientSettingRepo
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

sealed class ReminderResult {
    object NoReminder : ReminderResult()
    object RemindNoFood : ReminderResult()
    data class RemindBehindOnMacros(val percentComplete: Int) : ReminderResult()
}

class MealReminderChecker(
    private val foodRepository: FoodRepository,
    private val nutrientSettingRepo: NutrientSettingRepo
) {
    suspend fun shouldRemind(currentHour: Int): ReminderResult {
        // Time gate: only 7:00–21:00
        if (currentHour < 7 || currentHour >= 21) return ReminderResult.NoReminder

        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
            .toString()

        val foodItems = foodRepository.getFoodItemsForDate(today)

        // Inactivity check: if food logged within last 90 minutes, skip
        val now = Clock.System.now()
        val ninetyMinutesAgo = now.minus(kotlin.time.Duration.parse("90m"))
        val recentlyLogged = foodItems.any { it.createdAt >= ninetyMinutesAgo }
        if (recentlyLogged) return ReminderResult.NoReminder

        val totalCalories = foodRepository.getTotalCaloriesForDate(today)
        val settings = nutrientSettingRepo.getLatestNutrientSettings()

        // No targets set: only remind if 0 calories after 10:00
        if (settings == null || settings.targetCalories <= 0) {
            return if (totalCalories == 0 && currentHour >= 10) {
                ReminderResult.RemindNoFood
            } else {
                ReminderResult.NoReminder
            }
        }

        // Special: 0 calories after 10:00 always remind
        if (totalCalories == 0 && currentHour >= 10) {
            return ReminderResult.RemindNoFood
        }

        // Progress check
        val expectedFraction = (currentHour - 7) / 14.0
        val actualFraction = totalCalories.toDouble() / settings.targetCalories
        if (actualFraction < expectedFraction * 0.6) {
            val percentComplete = (actualFraction * 100).toInt()
            return ReminderResult.RemindBehindOnMacros(percentComplete)
        }

        return ReminderResult.NoReminder
    }
}
