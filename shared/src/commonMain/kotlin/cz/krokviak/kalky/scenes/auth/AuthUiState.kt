package cz.krokviak.kalky.scenes.auth

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.StateFlow

@Immutable
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
