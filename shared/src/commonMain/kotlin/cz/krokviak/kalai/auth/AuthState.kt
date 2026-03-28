package cz.krokviak.kalai.auth

import kotlinx.coroutines.flow.StateFlow

data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)

interface AuthStateProvider {
    val currentUser: StateFlow<AuthUser?>
    val isAuthenticated: StateFlow<Boolean>
}
