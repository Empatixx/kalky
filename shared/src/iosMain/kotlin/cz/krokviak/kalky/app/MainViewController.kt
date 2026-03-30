package cz.krokviak.kalky.app

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import cz.krokviak.kalky.analytics.AnalyticsViewModel
import cz.krokviak.kalky.auth.AuthViewModelInterface
import cz.krokviak.kalky.auth.StubAuthViewModel
import cz.krokviak.kalky.common.IosPlatformActions
import cz.krokviak.kalky.common.LocalPlatformActions
import cz.krokviak.kalky.common.PlatformActions
import cz.krokviak.kalky.customfood.CustomFoodViewModel
import cz.krokviak.kalky.detail.FoodDetailViewModel
import cz.krokviak.kalky.di.koinInject
import cz.krokviak.kalky.home.MainViewModel
import cz.krokviak.kalky.nutrientedit.NutrientEditViewModel
import cz.krokviak.kalky.onboarding.OnboardingViewModel
import cz.krokviak.kalky.settings.SettingsViewModel
import cz.krokviak.kalky.theme.KalkyTheme
import platform.UIKit.UIViewController

fun MainViewController(
    platformActions: PlatformActions = IosPlatformActions()
): UIViewController = ComposeUIViewController {
    KalkyTheme {
        CompositionLocalProvider(LocalPlatformActions provides platformActions) {
            val mainViewModel: MainViewModel = koinInject()
            val foodDetailViewModel: FoodDetailViewModel = koinInject()
            val nutrientEditViewModel: NutrientEditViewModel = koinInject()
            val analyticsViewModel: AnalyticsViewModel = koinInject()
            val settingsViewModel: SettingsViewModel = koinInject()
            val onboardingViewModel: OnboardingViewModel = koinInject()
            val customFoodViewModel: CustomFoodViewModel = koinInject()
            val authViewModel: AuthViewModelInterface = koinInject()

            AppContent(
                mainViewModel = mainViewModel,
                foodDetailViewModel = foodDetailViewModel,
                nutrientEditViewModel = nutrientEditViewModel,
                analyticsViewModel = analyticsViewModel,
                settingsViewModel = settingsViewModel,
                onboardingViewModel = onboardingViewModel,
                customFoodViewModel = customFoodViewModel,
                authViewModel = authViewModel,
            )
        }
    }
}
