package cz.krokviak.kalky.scenes.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthTokenProvider : AuthTokenProvider, AuthStateProvider {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableStateFlow(firebaseAuth.currentUser?.toAuthUser())
    override val currentUser: StateFlow<AuthUser?> = _currentUser

    private val _isAuthenticated = MutableStateFlow(firebaseAuth.currentUser != null)
    override val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _currentUser.value = auth.currentUser?.toAuthUser()
        _isAuthenticated.value = auth.currentUser != null
    }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
        // Set Crashlytics user ID for returning users
        firebaseAuth.currentUser?.uid?.let { Firebase.crashlytics.setUserId(it) }
    }

    override suspend fun getIdToken(): String? {
        val user = firebaseAuth.currentUser ?: return null
        return user.getIdToken(false).await().token
    }

    override fun isSignedIn(): Boolean = firebaseAuth.currentUser != null

    fun signOut() {
        firebaseAuth.signOut()
    }
}

private fun FirebaseUser.toAuthUser() = AuthUser(
    uid = uid,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl?.toString()
)
