import FirebaseAuth
import shared

/// Implements shared AuthTokenProvider interface using Firebase iOS SDK.
/// Pass this into KoinHelperKt.doInitKoinIos(authTokenProvider:...) at app launch.
class IosAuthTokenProvider: AuthTokenProvider {
    func getIdToken() async throws -> String? {
        guard let user = Auth.auth().currentUser else { return nil }
        return try await user.getIDToken()
    }

    func isSignedIn() -> Bool {
        Auth.auth().currentUser != nil
    }
}
