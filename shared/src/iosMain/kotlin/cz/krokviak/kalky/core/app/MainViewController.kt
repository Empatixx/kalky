package cz.krokviak.kalky.core.app

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import cz.krokviak.kalky.core.common.IosPlatformActions
import cz.krokviak.kalky.core.common.LocalPlatformActions
import cz.krokviak.kalky.core.common.PlatformActions
import cz.krokviak.kalky.core.theme.KalkyTheme
import platform.UIKit.UIViewController

fun MainViewController(
    platformActions: PlatformActions = IosPlatformActions()
): UIViewController = ComposeUIViewController {
    KalkyTheme {
        CompositionLocalProvider(LocalPlatformActions provides platformActions) {
            AppContent()
        }
    }
}
