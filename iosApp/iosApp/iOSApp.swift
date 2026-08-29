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
            liveActivityController: IosLiveActivityController(),
            backendBaseUrl: IosRemoteConfigManager.getBackendBaseUrl()
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    handleURL(url)
                }
        }
    }

    private func handleURL(_ url: URL) {
        guard url.scheme == "kalky" else {
            GIDSignIn.sharedInstance.handle(url)
            return
        }
        guard url.host == "food", let id = Int64(url.lastPathComponent) else { return }
        IosKoinResolversKt.openFoodDetailDeepLink(id: id)
    }
}
