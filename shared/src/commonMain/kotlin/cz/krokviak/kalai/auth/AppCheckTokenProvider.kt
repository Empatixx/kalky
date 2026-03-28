package cz.krokviak.kalai.auth

interface AppCheckTokenProvider {
    suspend fun getToken(): String?
}
