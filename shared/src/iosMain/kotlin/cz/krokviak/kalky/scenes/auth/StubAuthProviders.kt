package cz.krokviak.kalky.scenes.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StubAuthTokenProvider : AuthTokenProvider {
    override suspend fun getIdToken(): String? = null
    override fun isSignedIn(): Boolean = false
}

class StubAuthStateProvider : AuthStateProvider {
    override val currentUser: StateFlow<AuthUser?> = MutableStateFlow(null)
    override val isAuthenticated: StateFlow<Boolean> = MutableStateFlow(false)
}

class StubAppCheckTokenProvider : AppCheckTokenProvider {
    override suspend fun getToken(): String? = null
}

class StubAuthViewModel : AuthViewModelInterface {
    private val _uiState = MutableStateFlow(AuthUiState())
    override val uiState: StateFlow<AuthUiState> = _uiState
    override val authUser: StateFlow<AuthUser?> = MutableStateFlow(null)
    override fun clearError() {}
    override fun signOut() {}
    override fun onAuthSuccess() {}
    override fun onAuthError(message: String) {}
}
