package cz.krokviak.kalai.network

import cz.krokviak.kalai.auth.AuthTokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(authTokenProvider: AuthTokenProvider? = null): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }

        if (authTokenProvider != null) {
            install(createClientPlugin("AuthInterceptor") {
                on(Send) { request ->
                    val url = request.url.toString()
                    if (!url.contains("openfoodfacts") && authTokenProvider.isSignedIn()) {
                        val token = authTokenProvider.getIdToken()
                        if (token != null) {
                            request.header("Authorization", "Bearer $token")
                        }
                    }
                    proceed(request)
                }
            })
        }
    }
}
