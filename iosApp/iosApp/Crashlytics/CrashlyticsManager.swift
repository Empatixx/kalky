import Foundation
#if canImport(FirebaseCrashlytics)
import FirebaseCrashlytics
#endif

/// Mirrors the Android `Firebase.crashlytics.setUserId(...)` calls in
/// AuthViewModel + FirebaseAuthTokenProvider. The whole thing compiles to a
/// no-op until `FirebaseCrashlytics` is added to the Xcode project as a
/// Swift Package dependency — see `iosApp/CRASHLYTICS_SETUP.md` for the
/// step-by-step.
enum CrashlyticsManager {

    /// Call after a successful sign-in (Google, Apple, or auto-restore).
    static func setUserId(_ uid: String) {
        #if canImport(FirebaseCrashlytics)
        Crashlytics.crashlytics().setUserID(uid)
        #endif
    }

    /// Call on sign-out so further crashes aren't attributed to the previous user.
    static func clearUserId() {
        #if canImport(FirebaseCrashlytics)
        Crashlytics.crashlytics().setUserID("")
        #endif
    }

    /// Attach a custom key for filtering crashes in the dashboard.
    static func setKey(_ key: String, value: String) {
        #if canImport(FirebaseCrashlytics)
        Crashlytics.crashlytics().setCustomValue(value, forKey: key)
        #endif
    }

    /// Log a non-fatal error — useful for caught exceptions you still want to track.
    static func recordError(_ error: Error) {
        #if canImport(FirebaseCrashlytics)
        Crashlytics.crashlytics().record(error: error)
        #endif
    }
}
