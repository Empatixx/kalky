package cz.krokviak.kalai.auth

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import cz.krokviak.kalai.config.RemoteConfigManager
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val authTokenProvider: FirebaseAuthTokenProvider,
    private val httpClient: HttpClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val authUser: StateFlow<AuthUser?> = authTokenProvider.currentUser

    init {
        viewModelScope.launch {
            authTokenProvider.isAuthenticated.collect { authenticated ->
                _uiState.update { it.copy(isSignedIn = authenticated) }
            }
        }
    }

    fun signInWithGoogle(activity: Activity, webClientId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val credentialManager = CredentialManager.create(activity)
                val signInOption = GetSignInWithGoogleOption.Builder(webClientId).build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(signInOption)
                    .build()
                val result = credentialManager.getCredential(activity, request)
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()
                onAuthSuccess(authResult)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Google sign-in failed") }
            }
        }
    }

    private suspend fun onAuthSuccess(result: AuthResult) {
        try {
            httpClient.post(RemoteConfigManager.getBackendBaseUrl() + "/api/auth/me") {
                contentType(ContentType.Application.Json)
            }
        } catch (_: Exception) {
            // Backend registration failed but auth succeeded - still allow login
        }
        result.user?.uid?.let { Firebase.crashlytics.setUserId(it) }
        _uiState.update { it.copy(isLoading = false, isSignedIn = true) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun signOut() {
        Firebase.crashlytics.setUserId("")
        authTokenProvider.signOut()
    }
}
