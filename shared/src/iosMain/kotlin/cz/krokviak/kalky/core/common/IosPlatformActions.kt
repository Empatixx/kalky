package cz.krokviak.kalky.core.common

class IosPlatformActions(
    private val onLaunchCamera: () -> Unit = {},
    private val onLaunchBarcodeScanner: () -> Unit = {},
    private val onRequestNotificationPermission: () -> Unit = {},
    private val onShareImage: (String) -> Unit = {},
    private val onSignInWithGoogle: () -> Unit = {},
    private val onSignInWithApple: () -> Unit = {},
    private val onCheckNotificationPermission: () -> Boolean = { false }
) : PlatformActions {
    override fun launchCamera() = onLaunchCamera()
    override fun launchBarcodeScanner() = onLaunchBarcodeScanner()
    override fun requestNotificationPermission() = onRequestNotificationPermission()
    override fun shareImage(imagePath: String) = onShareImage(imagePath)
    override fun signInWithGoogle() = onSignInWithGoogle()
    override fun signInWithApple() = onSignInWithApple()
    override fun isNotificationPermissionGranted(): Boolean = onCheckNotificationPermission()
}
