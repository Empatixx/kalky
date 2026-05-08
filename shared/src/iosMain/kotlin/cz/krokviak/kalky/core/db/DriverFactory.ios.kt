@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package cz.krokviak.kalky.core.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        // Migrate old database filename
        val fileManager = platform.Foundation.NSFileManager.defaultManager
        val documentsDir = fileManager.URLsForDirectory(
            platform.Foundation.NSDocumentDirectory,
            platform.Foundation.NSUserDomainMask
        ).first() as platform.Foundation.NSURL
        val oldPath = documentsDir.URLByAppendingPathComponent("kalai.db")?.path
        val newPath = documentsDir.URLByAppendingPathComponent("kalky.db")?.path
        if (oldPath != null && newPath != null &&
            fileManager.fileExistsAtPath(oldPath) && !fileManager.fileExistsAtPath(newPath)) {
            fileManager.moveItemAtPath(oldPath, toPath = newPath, error = null)
        }
        return NativeSqliteDriver(KalkyDatabase.Schema, "kalky.db")
    }
}
