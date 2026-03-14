package cz.krokviak.kalai

import android.app.Application
import cz.krokviak.kalai.di.appModule
import cz.krokviak.kalai.di.sharedModule
import cz.krokviak.kalai.notifications.MealReminderScheduler
import cz.krokviak.kalai.notifications.NotificationHelper
import cz.krokviak.kalai.settings.AppPreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KalaiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppPreferencesManager.initialize(this)

        startKoin {
            androidContext(this@KalaiApplication)
            modules(sharedModule, appModule)
        }

        NotificationHelper.createNotificationChannel(this)
        if (AppPreferencesManager.notificationsEnabled.value) {
            MealReminderScheduler.schedule(this)
        }
    }
}
