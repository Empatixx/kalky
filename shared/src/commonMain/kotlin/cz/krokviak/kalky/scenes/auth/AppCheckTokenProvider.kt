package cz.krokviak.kalky.scenes.auth

interface AppCheckTokenProvider {
    suspend fun getToken(): String?
}
