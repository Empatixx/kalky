import FirebaseAuth
import shared

class IosAuthTokenProvider: AuthTokenProvider {
    func getIdToken() async throws -> String? {
        guard let user = Auth.auth().currentUser else { return nil }
        return try await user.getIDToken()
    }

    func isSignedIn() -> Bool {
        Auth.auth().currentUser != nil
    }
}
