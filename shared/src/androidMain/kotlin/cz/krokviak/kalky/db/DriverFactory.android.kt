package cz.krokviak.kalky.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        // Migrate old database filename
        val oldDb = context.getDatabasePath("kalai.db")
        val newDb = context.getDatabasePath("kalky.db")
        if (oldDb.exists() && !newDb.exists()) {
            oldDb.renameTo(newDb)
        }
        return AndroidSqliteDriver(KalkyDatabase.Schema, context, "kalky.db")
    }
}
