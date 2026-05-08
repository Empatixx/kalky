package cz.krokviak.kalky.core.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class KalkyFcmService : FirebaseMessagingService() {
    private val httpClient: HttpClient by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to backend
        CoroutineScope(Dispatchers.IO).launch {
            try {
                httpClient.post("${getBackendBaseUrl()}/api/auth/fcm-token") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"token":"$token"}""")
                }
            } catch (_: Exception) {
                // Will retry on next app start
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notification = message.notification ?: return
        NotificationHelper.showMealReminder(
            context = this,
            title = notification.title ?: return,
            body = notification.body ?: return
        )
    }

    private fun getBackendBaseUrl(): String {
        return try {
            cz.krokviak.kalky.config.RemoteConfigManager.getBackendBaseUrl()
        } catch (_: Exception) {
            "http://178.104.95.213"
        }
    }
}
