package cz.krokviak.kalky.scenes.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StubAuthTokenProvider : AuthTokenProvider {
    override suspend fun getIdToken(): String? = null
    override fun isSignedIn(): Boolean = false
}

class StubAuthStateProvider(authenticated: Boolean = false) : AuthStateProvider {
    override val currentUser: StateFlow<AuthUser?> = MutableStateFlow(null)
    override val isAuthenticated: StateFlow<Boolean> = MutableStateFlow(authenticated)
}

class StubAppCheckTokenProvider : AppCheckTokenProvider {
    override suspend fun getToken(): String? = null
}

class StubAuthViewModel : AuthViewModelInterface {
    private val _uiState = MutableStateFlow(AuthUiState())
    override val uiState: StateFlow<AuthUiState> = _uiState
    override val authUser: StateFlow<AuthUser?> = MutableStateFlow(null)
    override fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    override fun signOut() {
        _uiState.value = AuthUiState()
    }
    override fun onAuthSuccess() {
        _uiState.value = AuthUiState(isSignedIn = true)
    }
    override fun onAuthError(message: String) {
        _uiState.value = AuthUiState(error = message)
    }
}
