import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        KoinHelperKt.doInitKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
