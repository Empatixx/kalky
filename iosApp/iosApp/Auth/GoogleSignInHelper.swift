import GoogleSignIn
import FirebaseAuth
import FirebaseCore
import UIKit

/// Handles Google Sign-In flow on iOS.
/// Uses Google Sign-In SDK → exchanges for Firebase credential.
enum GoogleSignInHelper {

    static func signIn(presenting viewController: UIViewController) async throws {
        guard let clientID = FirebaseApp.app()?.options.clientID else {
            throw AuthError.missingClientID
        }

        let config = GIDConfiguration(clientID: clientID)
        GIDSignIn.sharedInstance.configuration = config

        let result = try await GIDSignIn.sharedInstance.signIn(withPresenting: viewController)

        guard let idToken = result.user.idToken?.tokenString else {
            throw AuthError.missingIDToken
        }

        let credential = GoogleAuthProvider.credential(
            withIDToken: idToken,
            accessToken: result.user.accessToken.tokenString
        )

        try await Auth.auth().signIn(with: credential)
    }

    enum AuthError: LocalizedError {
        case missingClientID
        case missingIDToken

        var errorDescription: String? {
            switch self {
            case .missingClientID: return "Firebase client ID not configured"
            case .missingIDToken: return "Google Sign-In did not return an ID token"
            }
        }
    }
}
