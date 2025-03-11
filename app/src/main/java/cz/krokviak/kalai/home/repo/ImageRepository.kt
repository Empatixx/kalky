package cz.krokviak.kalai.home.repo

import android.content.Context
import android.os.Environment
import cz.krokviak.kalai.camera.data.FoodAnalysisDto
import cz.krokviak.kalai.common.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import org.threeten.bp.OffsetDateTime
import java.io.File
import java.util.UUID

class ImageRepository(
    private val context: Context   // or pass Application, or do DI injection
) {
    /**
     * Save the given image bytes to a unique file and return its absolute path.
     */
    suspend fun saveImageFile(bytes: ByteArray): String = withContext(Dispatchers.IO) {
        val photosDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: throw IllegalStateException("Cannot access external files dir")

        val file = File(photosDir, "photo_${UUID.randomUUID()}.jpg")
        file.writeBytes(bytes)

        return@withContext file.absolutePath
    }

    /**
     * Call the server’s analysis API with the provided image bytes.
     * Return a [FoodAnalysisDto] if successful, or null if not.
     */
    suspend fun analyzeImage(bytes: ByteArray): FoodAnalysisDto? = withContext(Dispatchers.IO) {
        try {
            val requestBody = RequestBody.create(MediaType.parse("image/jpeg"), bytes)
            val response = RetrofitClient.instance.getAnalysis(requestBody).execute()
            if (response.isSuccessful) {
                response.body() // Might be null if the body is empty
            } else {
                null
            }
        } catch (e: Exception) {
            // Log or handle exception
            null
        }
    }

    /**
     * Optionally, if you want your repository to also handle DB updates,
     * you can put that logic here. For example:
     */
    suspend fun updateDatabaseWithAnalysisResult(
        itemId: Long,
        analysis: FoodAnalysisDto?
    ) {
        // e.g., fetch the entity from DB, update macros, save back
    }
}
