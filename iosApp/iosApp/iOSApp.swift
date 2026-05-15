import SwiftUI
import shared
import FirebaseCore
import FirebaseAppCheck
import GoogleSignIn

@main
struct iOSApp: App {
    @StateObject private var notificationManager = NotificationManager.shared

    init() {

        FirebaseApp.configure()
        let appCheckProviderFactory = AppCheckDebugProviderFactory()

        AppCheck.setAppCheckProviderFactory(appCheckProviderFactory)

        IosRemoteConfigManager.initialize()

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
