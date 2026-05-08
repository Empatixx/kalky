package cz.krokviak.kalky.scenes.auth

interface AuthTokenProvider {
    suspend fun getIdToken(): String?
    fun isSignedIn(): Boolean
}
