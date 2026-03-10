package cz.krokviak.kalai.network

import cz.krokviak.kalai.camera.data.FoodAnalysisDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class FoodAnalysisClient(private val httpClient: HttpClient) {
    suspend fun getAnalysis(imageBytes: ByteArray, baseUrl: String = "http://192.168.0.115:8080"): FoodAnalysisDto? {
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
