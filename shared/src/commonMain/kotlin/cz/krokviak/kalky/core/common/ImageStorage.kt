package cz.krokviak.kalky.core.common

interface ImageStorage {
    suspend fun storeImageFile(imageBytes: ByteArray): String
    suspend fun getImageBytes(imagePath: String): ByteArray
}
