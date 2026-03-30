import SwiftUI
import shared
import FirebaseCore
import FirebaseAppCheck
import GoogleSignIn

@main
struct iOSApp: App {
    @StateObject private var notificationManager = NotificationManager.shared

    init() {
        // Firebase
        FirebaseApp.configure()
        let appCheckProviderFactory = AppCheckDebugProviderFactory()
        // In production use: DeviceCheckProviderFactory()
        AppCheck.setAppCheckProviderFactory(appCheckProviderFactory)

        // Remote Config
        IosRemoteConfigManager.initialize()

        // Koin DI with real Firebase providers
        KoinHelperKt.doInitKoinIos(
            authTokenProvider: IosAuthTokenProvider(),
            authStateProvider: IosAuthStateProvider(),
            appCheckTokenProvider: IosAppCheckTokenProvider(),
            backendBaseUrl: IosRemoteConfigManager.getBackendBaseUrl()
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    GIDSignIn.sharedInstance.handle(url)
                }
        }
    }
}
