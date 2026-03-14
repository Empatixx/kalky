package cz.krokviak.kalai.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cz.krokviak.kalai.settings.AppPreferencesManager
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class MealReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    override suspend fun doWork(): Result {
        if (!AppPreferencesManager.notificationsEnabled.value) {
            return Result.success()
        }

        val now = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val currentHour = now.hour

        // Time gate
        if (currentHour < 7 || currentHour >= 21) {
            return Result.success()
        }

        // Cooldown: no repeat within 3 hours
        val lastNotificationTime = AppPreferencesManager.getLastNotificationTime()
        val threeHoursMillis = 3 * 60 * 60 * 1000L
        if (lastNotificationTime > 0 &&
            System.currentTimeMillis() - lastNotificationTime < threeHoursMillis
        ) {
            return Result.success()
        }

        val checker: MealReminderChecker = get()
        when (val result = checker.shouldRemind(currentHour)) {
            is ReminderResult.NoReminder -> { /* do nothing */ }
            is ReminderResult.RemindNoFood -> {
                NotificationHelper.showMealReminder(
                    applicationContext,
                    "Zapomnel/a jsi jist?",
                    "Dnes jsi jeste nic nezaznamenal/a. Pridej si jidlo!"
                )
                AppPreferencesManager.setLastNotificationTime(System.currentTimeMillis())
            }
            is ReminderResult.RemindBehindOnMacros -> {
                NotificationHelper.showMealReminder(
                    applicationContext,
                    "Jsi pozadu s jidlem",
                    "Mas za sebou jen ${result.percentComplete}% kalorii. Nezapomen jist!"
                )
                AppPreferencesManager.setLastNotificationTime(System.currentTimeMillis())
            }
        }

        return Result.success()
    }
}
