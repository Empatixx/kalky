package cz.krokviak.kalai.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    init {
        viewModelScope.launch {
            authTokenProvider.isAuthenticated.collect { authenticated ->
                _uiState.update { it.copy(isSignedIn = authenticated) }
            }
        }
    }

    fun onFirebaseAuthSuccess(result: AuthResult) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Register user on backend
                httpClient.post("http://192.168.0.115:3000/api/auth/me") {
                    contentType(ContentType.Application.Json)
                }
                _uiState.update { it.copy(isLoading = false, isSignedIn = true) }
            } catch (e: Exception) {
                // Backend registration failed but auth succeeded - still allow login
                _uiState.update { it.copy(isLoading = false, isSignedIn = true) }
            }
        }
    }

    fun onAuthError(error: String) {
        _uiState.update { it.copy(isLoading = false, error = error) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun signOut() {
        authTokenProvider.signOut()
    }
}
