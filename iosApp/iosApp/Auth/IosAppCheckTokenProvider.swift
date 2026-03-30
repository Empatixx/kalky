import FirebaseAppCheck
import shared

/// Implements shared AppCheckTokenProvider interface using Firebase iOS SDK.
class IosAppCheckTokenProvider: AppCheckTokenProvider_ {
    func getToken() async throws -> String? {
        let token = try await AppCheck.appCheck().token(forcingRefresh: false)
        return token.token
    }
}
