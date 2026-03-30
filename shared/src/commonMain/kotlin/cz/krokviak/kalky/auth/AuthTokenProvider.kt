package cz.krokviak.kalky.auth

interface AuthTokenProvider {
    suspend fun getIdToken(): String?
    fun isSignedIn(): Boolean
}
