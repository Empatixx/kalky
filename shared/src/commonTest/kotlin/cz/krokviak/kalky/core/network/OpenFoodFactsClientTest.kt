package cz.krokviak.kalky.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OpenFoodFactsClientTest {

    private val jsonHeaders = headersOf("Content-Type", "application/json")

    private fun buildHttpClient(engine: MockEngine): HttpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    @Test
    fun getProduct_returnsParsedProduct_whenStatusOne() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/api/v2/product/8594001234567.json", request.url.encodedPath)
            respond(
                content = """
                    {
                      "status": 1,
                      "product": {
                        "product_name": "Mléko",
                        "nutriments": {
                          "energy-kcal_100g": 42.0,
                          "proteins_100g": 3.4,
                          "fat_100g": 1.5,
                          "carbohydrates_100g": 4.8
                        }
                      }
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = jsonHeaders
            )
        }
        val client = OpenFoodFactsClient(buildHttpClient(engine))

        val product = client.getProduct("8594001234567")

        assertEquals("Mléko", product?.productName)
        assertEquals(42.0, product?.nutriments?.energyKcal100g)
        assertEquals(3.4, product?.nutriments?.proteins100g)
    }

    @Test
    fun getProduct_returnsNull_whenStatusZero() = runTest {
        val engine = MockEngine {
            respond(
                content = """{"status": 0}""",
                status = HttpStatusCode.OK,
                headers = jsonHeaders
            )
        }
        val client = OpenFoodFactsClient(buildHttpClient(engine))

        val product = client.getProduct("0000")

        assertNull(product)
    }

    @Test
    fun getProduct_returnsNull_onServerError() = runTest {
        val engine = MockEngine {
            respond(content = "", status = HttpStatusCode.InternalServerError)
        }
        val client = OpenFoodFactsClient(buildHttpClient(engine))

        val product = client.getProduct("8594001234567")

        assertNull(product)
    }

    @Test
    fun searchProducts_returnsParsedHits_andFiltersBlankNames() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/search", request.url.encodedPath)
            assertEquals("mléko", request.url.parameters["q"])
            assertEquals("20", request.url.parameters["page_size"])
            respond(
                content = """
                    {
                      "count": 3,
                      "hits": [
                        {"product_name": "Mléko polotučné", "nutriments": {"energy-kcal_100g": 46.0}},
                        {"product_name": "", "nutriments": null},
                        {"product_name": "Mléko plnotučné", "nutriments": {"energy-kcal_100g": 65.0}}
                      ]
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = jsonHeaders
            )
        }
        val client = OpenFoodFactsClient(buildHttpClient(engine))

        val hits = client.searchProducts("mléko")

        assertEquals(2, hits.size)
        assertTrue(hits.all { !it.productName.isNullOrBlank() })
        assertEquals("Mléko polotučné", hits[0].productName)
    }

    @Test
    fun searchProducts_returnsEmptyList_onServerError() = runTest {
        val engine = MockEngine {
            respond(content = "", status = HttpStatusCode.BadGateway)
        }
        val client = OpenFoodFactsClient(buildHttpClient(engine))

        val hits = client.searchProducts("anything")

        assertTrue(hits.isEmpty())
    }
}
