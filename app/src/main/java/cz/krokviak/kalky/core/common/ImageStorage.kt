package cz.krokviak.kalky.core.common

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class AndroidImageStorage(private val context: Context) : ImageStorage {

    override suspend fun storeImageFile(imageBytes: ByteArray): String = withContext(Dispatchers.IO) {
        val photosDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: throw IllegalStateException("Cannot access external files directory")

        val file = File(photosDir, "photo_${UUID.randomUUID()}.jpg")
        file.writeBytes(imageBytes)
        file.absolutePath
    }

    override suspend fun getImageBytes(imagePath: String): ByteArray = withContext(Dispatchers.IO) {
        File(imagePath).readBytes()
    }
}
