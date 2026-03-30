import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        KoinHelperKt.doInitKoinIos(
            authTokenProvider: nil,
            authStateProvider: nil,
            appCheckTokenProvider: nil,
            backendBaseUrl: nil
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
