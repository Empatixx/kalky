import FirebaseAuth
import shared

class IosAuthStateProvider: AuthStateProvider {
    private let holder = IosAuthStateHolder()
    private var handle: AuthStateDidChangeListenerHandle?

    var currentUser: any Kotlinx_coroutines_coreStateFlow { holder.currentUser }
    var isAuthenticated: any Kotlinx_coroutines_coreStateFlow { holder.isAuthenticated }

    init() {
        if let user = Auth.auth().currentUser {
            holder.setUser(user: AuthUser(
                uid: user.uid,
                email: user.email,
                displayName: user.displayName,
                photoUrl: user.photoURL?.absoluteString
            ))
        }
        handle = Auth.auth().addStateDidChangeListener { [weak self] _, user in
            if let user = user {
                let authUser = AuthUser(
                    uid: user.uid,
                    email: user.email,
                    displayName: user.displayName,
                    photoUrl: user.photoURL?.absoluteString
                )
                self?.holder.setUser(user: authUser)
                CrashlyticsManager.setUserId(user.uid)
            } else {
                self?.holder.setUser(user: nil)
                CrashlyticsManager.clearUserId()
            }
        }
    }

    deinit {
        if let handle = handle {
            Auth.auth().removeStateDidChangeListener(handle)
        }
    }
}
