package cz.krokviak.kalky.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FoodAnalysisClientTest {

    private val jsonHeaders = headersOf("Content-Type", "application/json")

    private fun buildHttpClient(engine: MockEngine): HttpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    @Test
    fun getAnalysis_postsJpegBytes_andDeserializesResponse() = runTest {
        val payload = byteArrayOf(1, 2, 3, 4, 5)
        val engine = MockEngine { request ->
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("/cal", request.url.encodedPath)
            assertEquals(ContentType.Image.JPEG, request.body.contentType)
            respond(
                content = """
                    {
                      "weight": 200,
                      "foodType": "fruit",
                      "title": "Avokádo",
                      "protein": 4,
                      "fat": 22,
                      "carbs": 12,
                      "healthScore": 8
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = jsonHeaders
            )
        }
        val client = FoodAnalysisClient(buildHttpClient(engine), baseUrl = "http://test.local")

        val result = client.getAnalysis(payload)

        assertNotNull(result)
        assertEquals("Avokádo", result.title)
        assertEquals(200, result.weight)
        assertEquals(8, result.healthScore)

        assertEquals(262, result.calories)
    }

    @Test
    fun getAnalysis_returnsNull_onServerError() = runTest {
        val engine = MockEngine {
            respond(content = "", status = HttpStatusCode.InternalServerError)
        }
        val client = FoodAnalysisClient(buildHttpClient(engine), baseUrl = "http://test.local")

        val result = client.getAnalysis(byteArrayOf(1, 2, 3))

        assertNull(result)
    }

    @Test
    fun getAnalysis_returnsNull_onMalformedJson() = runTest {
        val engine = MockEngine {
            respond(
                content = "not json",
                status = HttpStatusCode.OK,
                headers = jsonHeaders
            )
        }
        val client = FoodAnalysisClient(buildHttpClient(engine), baseUrl = "http://test.local")

        val result = client.getAnalysis(byteArrayOf(1, 2, 3))

        assertNull(result)
    }

    @Test
    fun getAnalysis_usesCustomBaseUrl() = runTest {
        val engine = MockEngine { request ->
            assertEquals("custom.host", request.url.host)
            respond(
                content = """{"weight":0,"foodType":"","title":"","protein":0,"fat":0,"carbs":0,"healthScore":0}""",
                status = HttpStatusCode.OK,
                headers = jsonHeaders
            )
        }
        val client = FoodAnalysisClient(buildHttpClient(engine), baseUrl = "https://custom.host")

        val result = client.getAnalysis(byteArrayOf(0))

        assertNotNull(result)
    }
}
