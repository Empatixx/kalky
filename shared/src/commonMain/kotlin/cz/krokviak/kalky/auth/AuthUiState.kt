package cz.krokviak.kalky.auth

import kotlinx.coroutines.flow.StateFlow

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val error: String? = null
)

interface AuthViewModelInterface {
    val uiState: StateFlow<AuthUiState>
    val authUser: StateFlow<AuthUser?>
    fun clearError()
    fun signOut()
    fun onAuthSuccess()
    fun onAuthError(message: String)
}
