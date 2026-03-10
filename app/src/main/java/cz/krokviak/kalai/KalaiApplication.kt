package cz.krokviak.kalai

import android.app.Application
import cz.krokviak.kalai.di.appModule
import cz.krokviak.kalai.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KalaiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@KalaiApplication)
            modules(sharedModule, appModule)
        }
    }
}
