package cz.krokviak.kalky.scenes.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class IosAuthStateHolder : AuthStateProvider {
    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    private val _isAuthenticated = MutableStateFlow(false)

    override val currentUser: StateFlow<AuthUser?> = _currentUser
    override val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    fun setUser(user: AuthUser?) {
        _currentUser.value = user
        _isAuthenticated.value = user != null
    }
}
