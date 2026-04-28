package cz.krokviak.kalky.network

import cz.krokviak.kalky.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalky.barcode.data.OpenFoodFactsResponse
import cz.krokviak.kalky.barcode.data.OpenFoodFactsSearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class OpenFoodFactsClient(private val httpClient: HttpClient) {
    private val productMutex = Mutex()
    private val searchMutex = Mutex()
    private val productInflight = mutableMapOf<String, CompletableDeferred<OpenFoodFactsProduct?>>()
    private val searchInflight = mutableMapOf<SearchKey, CompletableDeferred<List<OpenFoodFactsProduct>>>()

    suspend fun getProduct(barcode: String): OpenFoodFactsProduct? {
        val cached = productMutex.withLock { productInflight[barcode] }
        if (cached != null) return cached.await()

        val deferred = CompletableDeferred<OpenFoodFactsProduct?>()
        productMutex.withLock { productInflight[barcode] = deferred }
        val result = fetchProduct(barcode)
        deferred.complete(result)
        productMutex.withLock { productInflight.remove(barcode) }
        return result
    }

    suspend fun searchProducts(query: String, pageSize: Int = 20): List<OpenFoodFactsProduct> {
        val key = SearchKey(query, pageSize)
        val cached = searchMutex.withLock { searchInflight[key] }
        if (cached != null) return cached.await()

        val deferred = CompletableDeferred<List<OpenFoodFactsProduct>>()
        searchMutex.withLock { searchInflight[key] = deferred }
        val result = fetchSearch(query, pageSize)
        deferred.complete(result)
        searchMutex.withLock { searchInflight.remove(key) }
        return result
    }

    private suspend fun fetchProduct(barcode: String): OpenFoodFactsProduct? = runCatching {
        val response: OpenFoodFactsResponse =
            httpClient.get("https://world.openfoodfacts.org/api/v2/product/$barcode.json").body()
        if (response.status == 1) response.product else null
    }.getOrNull()

    private suspend fun fetchSearch(query: String, pageSize: Int): List<OpenFoodFactsProduct> = runCatching {
        val response: OpenFoodFactsSearchResponse =
            httpClient.get("https://search.openfoodfacts.org/search") {
                parameter("q", query)
                parameter("page_size", pageSize)
                parameter("fields", "product_name,nutriments")
            }.body()
        response.hits.filter { !it.productName.isNullOrBlank() }
    }.getOrDefault(emptyList())

    private data class SearchKey(val query: String, val pageSize: Int)
}
