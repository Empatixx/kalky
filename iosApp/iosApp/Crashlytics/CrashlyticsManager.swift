import Foundation
#if canImport(FirebaseCrashlytics)
import FirebaseCrashlytics
#endif

enum CrashlyticsManager {

    static func setUserId(_ uid: String) {
        #if canImport(FirebaseCrashlytics)
        Crashlytics.crashlytics().setUserID(uid)
        #endif
    }

    static func clearUserId() {
        #if canImport(FirebaseCrashlytics)
        Crashlytics.crashlytics().setUserID("")
        #endif
    }

    static func setKey(_ key: String, value: String) {
        #if canImport(FirebaseCrashlytics)
        Crashlytics.crashlytics().setCustomValue(value, forKey: key)
        #endif
    }

    static func recordError(_ error: Error) {
        #if canImport(FirebaseCrashlytics)
        Crashlytics.crashlytics().record(error: error)
        #endif
    }
}
