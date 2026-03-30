package cz.krokviak.kalky

import android.app.Application
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import cz.krokviak.kalky.config.RemoteConfigManager
import cz.krokviak.kalky.di.appModule
import cz.krokviak.kalky.di.sharedModule
import cz.krokviak.kalky.notifications.MealReminderScheduler
import cz.krokviak.kalky.notifications.NotificationHelper
import cz.krokviak.kalky.settings.AppPreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KalkyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppPreferencesManager.initialize(this)

        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        startKoin {
            androidContext(this@KalkyApplication)
            modules(sharedModule, appModule)
        }

        RemoteConfigManager.init()

        NotificationHelper.createNotificationChannel(this)
        if (AppPreferencesManager.notificationsEnabled.value) {
            MealReminderScheduler.schedule(this)
        }
    }
}
