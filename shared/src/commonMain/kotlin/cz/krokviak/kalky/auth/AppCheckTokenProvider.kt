package cz.krokviak.kalky.auth

interface AppCheckTokenProvider {
    suspend fun getToken(): String?
}
