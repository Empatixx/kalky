package cz.krokviak.kalai.network

import cz.krokviak.kalai.camera.data.FoodAnalysisDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class FoodAnalysisClient(
    private val httpClient: HttpClient,
    private val baseUrl: String = DEFAULT_BASE_URL
) {
    companion object {
        const val DEFAULT_BASE_URL = "http://192.168.0.115:3000"
    }

    suspend fun getAnalysis(imageBytes: ByteArray): FoodAnalysisDto? {
        return try {
            httpClient.post("$baseUrl/cal") {
                contentType(ContentType.Image.JPEG)
                setBody(imageBytes)
            }.body()
        } catch (e: Exception) {
            null
        }
    }
}
