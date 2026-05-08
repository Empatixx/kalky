package cz.krokviak.kalky.core.network

import cz.krokviak.kalky.core.camera.data.FoodAnalysisDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

open class FoodAnalysisClient(
    private val httpClient: HttpClient,
    private val baseUrl: String = DEFAULT_BASE_URL
) {
    companion object {
        const val DEFAULT_BASE_URL = "http://178.104.95.213"
    }

    open suspend fun getAnalysis(imageBytes: ByteArray): FoodAnalysisDto? {
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
