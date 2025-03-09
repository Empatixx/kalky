package cz.krokviak.kalai.common

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val instance: AppDatabase by lazy {
        Room.databaseBuilder(appContext, AppDatabase::class.java, "kalai")
            .build()
    }
}
