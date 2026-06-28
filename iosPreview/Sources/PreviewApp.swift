import SwiftUI
import shared

@main
struct KalkyPreviewApp: App {
    init() {
        KoinHelperKt.doInitKoinIos(
            authTokenProvider: nil,
            authStateProvider: StubAuthStateProvider(authenticated: true),
            appCheckTokenProvider: nil,
            backendBaseUrl: nil
        )
    }

    var body: some Scene {
        WindowGroup {
            ComposeContainer()
                .ignoresSafeArea(.all)
        }
    }
}

struct ComposeContainer: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(
            platformActions: IosPlatformActions(
                onLaunchCamera: {},
                onLaunchBarcodeScanner: {},
                onRequestNotificationPermission: {},
                onShareImage: { _ in },
                onSignInWithGoogle: {},
                onSignInWithApple: {},
                onCheckNotificationPermission: { false }
            )
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
