package cz.krokviak.kalky

import android.app.Application
import cz.krokviak.kalky.config.RemoteConfigManager
import cz.krokviak.kalky.core.di.appModule
import cz.krokviak.kalky.core.di.sharedModule
import cz.krokviak.kalky.core.notifications.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KalkyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RemoteConfigManager.configure()

        startKoin {
            androidContext(this@KalkyApplication)
            modules(sharedModule, appModule)
        }

        NotificationHelper.createNotificationChannel(this)
    }
}
