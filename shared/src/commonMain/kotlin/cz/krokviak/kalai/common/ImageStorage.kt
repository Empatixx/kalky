package cz.krokviak.kalai.common

interface ImageStorage {
    suspend fun storeImageFile(imageBytes: ByteArray): String
    suspend fun getImageBytes(imagePath: String): ByteArray
}
