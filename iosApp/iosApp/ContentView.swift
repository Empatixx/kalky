import SwiftUI
import shared
import AVFoundation

struct ComposeView: UIViewControllerRepresentable {
    @Binding var showCamera: Bool
    @Binding var cameraMode: CameraMode

    func makeUIViewController(context: Context) -> UIViewController {
        let vc = MainViewControllerKt.MainViewController(
            platformActions: createPlatformActions()
        )
        context.coordinator.composeVC = vc
        return vc
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(parent: self)
    }

    private func createPlatformActions() -> IosPlatformActions {
        return IosPlatformActions(
            onLaunchCamera: {
                cameraMode = .photo
                showCamera = true
            },
            onLaunchBarcodeScanner: {
                cameraMode = .barcode
                showCamera = true
            },
            onRequestNotificationPermission: {
                NotificationManager.shared.requestPermission()
            },
            onShareImage: { path in
                guard let image = UIImage(contentsOfFile: path) else { return }
                let activityVC = UIActivityViewController(activityItems: [image], applicationActivities: nil)
                if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                   let rootVC = windowScene.windows.first?.rootViewController {
                    rootVC.present(activityVC, animated: true)
                }
            },
            onSignInWithGoogle: {
                guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                      let rootVC = windowScene.windows.first?.rootViewController else { return }
                Task {
                    do {
                        try await GoogleSignInHelper.signIn(presenting: rootVC)
                        let authVM: AuthViewModelInterface = KoinHelper.resolve()
                        authVM.onAuthSuccess()
                    } catch {
                        let authVM: AuthViewModelInterface = KoinHelper.resolve()
                        authVM.onAuthError(message: error.localizedDescription)
                    }
                }
            },
            onSignInWithApple: {
                Task {
                    do {
                        let helper = AppleSignInHelper()
                        try await helper.signIn()
                        let authVM: AuthViewModelInterface = KoinHelper.resolve()
                        authVM.onAuthSuccess()
                    } catch {
                        let authVM: AuthViewModelInterface = KoinHelper.resolve()
                        authVM.onAuthError(message: error.localizedDescription)
                    }
                }
            },
            onCheckNotificationPermission: {
                return NotificationManager.shared.isAuthorized
            }
        )
    }

    class Coordinator {
        var composeVC: UIViewController?
        let parent: ComposeView

        init(parent: ComposeView) {
            self.parent = parent
        }
    }
}

struct ContentView: View {
    @State private var showCamera = false
    @State private var cameraMode: CameraMode = .photo

    var body: some View {
        ComposeView(showCamera: $showCamera, cameraMode: $cameraMode)
            .ignoresSafeArea(.all)
            .fullScreenCover(isPresented: $showCamera) {
                CameraViewWrapper(mode: cameraMode) { jpegData in
                    handlePhotoCaptured(jpegData)
                } onBarcodeDetected: { barcode in
                    handleBarcodeDetected(barcode)
                }
            }
    }

    private func handlePhotoCaptured(_ data: Data) {
        showCamera = false
        // Convert Swift Data to Kotlin ByteArray and call shared ViewModel
        let kotlinBytes = DataToByteArray.convert(data)
        let mainViewModel: MainViewModel = KoinHelper.resolve()
        mainViewModel.addFoodItemFromBytes(imageBytes: kotlinBytes)
    }

    private func handleBarcodeDetected(_ barcode: String) {
        showCamera = false
        // Use shared BarcodeScannerViewModel for product lookup
        let barcodeVM: BarcodeScannerViewModel = KoinHelper.resolve()
        barcodeVM.onBarcodeDetected(barcode: barcode)
    }
}

// MARK: - Camera SwiftUI Wrapper

struct CameraViewWrapper: UIViewControllerRepresentable {
    let mode: CameraMode
    let onPhotoCaptured: (Data) -> Void
    let onBarcodeDetected: (String) -> Void

    func makeUIViewController(context: Context) -> KalkyCameraViewController {
        let vc = KalkyCameraViewController()
        vc.onPhotoCaptured = onPhotoCaptured
        vc.onBarcodeDetected = onBarcodeDetected
        vc.modalPresentationStyle = .fullScreen
        return vc
    }

    func updateUIViewController(_ uiViewController: KalkyCameraViewController, context: Context) {}
}

// MARK: - Koin Helper for Swift

enum KoinHelper {
    static func resolve<T: AnyObject>() -> T {
        let koin = KoinPlatformKt.getKoin()
        guard let instance = koin.get(objCClass: T.self) as? T else {
            fatalError("Koin: Could not resolve \(T.self)")
        }
        return instance
    }
}

// MARK: - Data <-> ByteArray Conversion

enum DataToByteArray {
    static func convert(_ data: Data) -> KotlinByteArray {
        let bytes = KotlinByteArray(size: Int32(data.count))
        data.withUnsafeBytes { buffer in
            guard let baseAddress = buffer.baseAddress else { return }
            for i in 0..<data.count {
                bytes.set(index: Int32(i), value: baseAddress.load(fromByteOffset: i, as: Int8.self))
            }
        }
        return bytes
    }
}
