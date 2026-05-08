package cz.krokviak.kalky.core.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object MealReminderScheduler {
    private const val WORK_TAG = "meal_reminder"

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<MealReminderWorker>(
            3, TimeUnit.HOURS
        ).addTag(WORK_TAG).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG)
    }
}
