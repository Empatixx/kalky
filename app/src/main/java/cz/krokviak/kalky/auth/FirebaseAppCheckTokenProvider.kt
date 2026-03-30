package cz.krokviak.kalky.auth

import com.google.firebase.appcheck.FirebaseAppCheck
import kotlinx.coroutines.tasks.await

class FirebaseAppCheckTokenProvider : AppCheckTokenProvider {
    override suspend fun getToken(): String? {
        return try {
            FirebaseAppCheck.getInstance()
                .getAppCheckToken(false)
                .await()
                .token
        } catch (_: Exception) {
            null
        }
    }
}
