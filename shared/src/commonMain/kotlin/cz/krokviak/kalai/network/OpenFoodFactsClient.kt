package cz.krokviak.kalai.network

import cz.krokviak.kalai.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalai.barcode.data.OpenFoodFactsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class OpenFoodFactsClient(private val httpClient: HttpClient) {
    suspend fun getProduct(barcode: String): OpenFoodFactsProduct? {
        return try {
            val response: OpenFoodFactsResponse =
                httpClient.get("https://world.openfoodfacts.org/api/v2/product/$barcode.json").body()
            if (response.status == 1) response.product else null
        } catch (e: Exception) {
            null
        }
    }
}
