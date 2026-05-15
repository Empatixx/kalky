import FirebaseAuth
import shared
import Combine

class IosAuthStateProvider: AuthStateProvider {
    private var handle: AuthStateDidChangeListenerHandle?
    private let _currentUser = FlowWrapper<AuthUser>(initial: nil)
    private let _isAuthenticated = FlowWrapper<KotlinBoolean>(initial: KotlinBoolean(value: false))

    var currentUser_: any Kotlinx_coroutines_coreStateFlow { _currentUser.flow }
    var isAuthenticated_: any Kotlinx_coroutines_coreStateFlow { _isAuthenticated.flow }

    init() {
        handle = Auth.auth().addStateDidChangeListener { [weak self] _, user in
            if let user = user {
                let authUser = AuthUser(
                    uid: user.uid,
                    email: user.email,
                    displayName: user.displayName,
                    photoUrl: user.photoURL?.absoluteString
                )
                self?._currentUser.emit(authUser)
                self?._isAuthenticated.emit(KotlinBoolean(value: true))

                CrashlyticsManager.setUserId(user.uid)
            } else {
                self?._currentUser.emit(nil)
                self?._isAuthenticated.emit(KotlinBoolean(value: false))
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

class FlowWrapper<T: AnyObject> {
    let flow: any Kotlinx_coroutines_coreStateFlow

    init(initial: T?) {
        let mutableFlow = MutableStateFlowKt.MutableStateFlow(value: initial)
        self.flow = mutableFlow
    }

    func emit(_ value: T?) {
        (flow as! Kotlinx_coroutines_coreMutableStateFlow).setValue(value)
    }
}
