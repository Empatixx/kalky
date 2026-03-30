package cz.krokviak.kalky.network

import cz.krokviak.kalky.auth.AppCheckTokenProvider
import cz.krokviak.kalky.auth.AuthTokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(
    authTokenProvider: AuthTokenProvider? = null,
    appCheckTokenProvider: AppCheckTokenProvider? = null,
): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }

        if (authTokenProvider != null || appCheckTokenProvider != null) {
            install(createClientPlugin("AuthInterceptor") {
                on(Send) { request ->
                    val url = request.url.toString()
                    if (!url.contains("openfoodfacts")) {
                        if (authTokenProvider != null && authTokenProvider.isSignedIn()) {
                            val token = authTokenProvider.getIdToken()
                            if (token != null) {
                                request.header("Authorization", "Bearer $token")
                            }
                        }
                        if (appCheckTokenProvider != null) {
                            val appCheckToken = appCheckTokenProvider.getToken()
                            if (appCheckToken != null) {
                                request.header("X-Firebase-AppCheck", appCheckToken)
                            }
                        }
                    }
                    proceed(request)
                }
            })
        }
    }
}
