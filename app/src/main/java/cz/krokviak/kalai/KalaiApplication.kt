package cz.krokviak.kalai

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import cz.krokviak.kalai.common.DatabaseProvider
import cz.krokviak.kalai.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KalaiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseProvider.init(this)
        AndroidThreeTen.init(this)
        startKoin {
            androidContext(this@KalaiApplication)
            modules(appModule)
        }
    }
}
