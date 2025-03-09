package cz.krokviak.kalai

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import cz.krokviak.kalai.common.DatabaseProvider

class KalaiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseProvider.init(this)
        AndroidThreeTen.init(this)
    }
}
