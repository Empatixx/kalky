package cz.krokviak.kalai.auth

interface AuthTokenProvider {
    suspend fun getIdToken(): String?
    fun isSignedIn(): Boolean
}
