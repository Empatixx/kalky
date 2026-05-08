package cz.krokviak.kalky.core.common

import androidx.compose.runtime.compositionLocalOf

interface PlatformActions {
    fun launchCamera()
    fun launchBarcodeScanner()
    fun requestNotificationPermission()
    fun shareImage(imagePath: String)
    fun signInWithGoogle()
    fun signInWithApple()
    fun isNotificationPermissionGranted(): Boolean
}

val LocalPlatformActions = compositionLocalOf<PlatformActions> {
    error("PlatformActions not provided")
}
