package cz.krokviak.kalai

import android.app.Application
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import cz.krokviak.kalai.config.RemoteConfigManager
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

        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        startKoin {
            androidContext(this@KalaiApplication)
            modules(sharedModule, appModule)
        }

        RemoteConfigManager.init()

        NotificationHelper.createNotificationChannel(this)
        if (AppPreferencesManager.notificationsEnabled.value) {
            MealReminderScheduler.schedule(this)
        }
    }
}
