package cz.krokviak.kalky.core.notifications

import cz.krokviak.kalky.core.common.repo.FoodRepository
import cz.krokviak.kalky.core.common.repo.NutrientSettingRepo
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
        if (loggedRecently(foodItems)) return ReminderResult.NoReminder

        // Reuse already-loaded items for the totalCalories sum — avoids a second DB hit.
        val totalCalories = foodItems.sumOf { it.calories }
        if (noFoodAlert(totalCalories, currentHour)) return ReminderResult.RemindNoFood

        val settings = nutrientSettingRepo.getLatestNutrientSettings()
        val targetCalories = settings?.targetCalories?.takeIf { it > 0 }
            ?: return ReminderResult.NoReminder

        return progressReminder(totalCalories, targetCalories, currentHour)
    }

    private fun loggedRecently(foodItems: List<cz.krokviak.kalky.core.common.entities.FoodItemEntity>): Boolean {
        val ninetyMinutesAgo = Clock.System.now().minus(kotlin.time.Duration.parse("90m"))
        return foodItems.any { it.createdAt >= ninetyMinutesAgo }
    }

    private fun noFoodAlert(totalCalories: Int, currentHour: Int): Boolean =
        totalCalories == 0 && currentHour >= 10

    private fun progressReminder(totalCalories: Int, targetCalories: Int, currentHour: Int): ReminderResult {
        val expectedFraction = (currentHour - 7) / 14.0
        val actualFraction = totalCalories.toDouble() / targetCalories
        if (actualFraction >= expectedFraction * 0.6) return ReminderResult.NoReminder
        val percentComplete = (actualFraction * 100).toInt()
        return ReminderResult.RemindBehindOnMacros(percentComplete)
    }
}
