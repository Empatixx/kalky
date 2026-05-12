package cz.krokviak.kalky.core.app

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import cz.krokviak.kalky.scenes.analytics.AnalyticsViewModel
import cz.krokviak.kalky.scenes.auth.AuthViewModelInterface
import cz.krokviak.kalky.core.common.IosPlatformActions
import cz.krokviak.kalky.core.common.LocalPlatformActions
import cz.krokviak.kalky.core.common.PlatformActions
import cz.krokviak.kalky.scenes.customfood.CustomFoodSearchViewModel
import cz.krokviak.kalky.scenes.customfood.ManualFoodEntryViewModel
import cz.krokviak.kalky.scenes.detail.FoodDetailViewModel
import cz.krokviak.kalky.core.di.koinInject
import cz.krokviak.kalky.scenes.home.MainViewModel
import cz.krokviak.kalky.scenes.nutrientedit.NutrientEditViewModel
import cz.krokviak.kalky.scenes.onboarding.OnboardingViewModel
import cz.krokviak.kalky.scenes.settings.SettingsViewModel
import cz.krokviak.kalky.core.theme.KalkyTheme
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
            val customFoodSearchViewModel: CustomFoodSearchViewModel = koinInject()
            val manualEntryViewModel: ManualFoodEntryViewModel = koinInject()
            val authViewModel: AuthViewModelInterface = koinInject()

            AppContent(
                mainViewModel = mainViewModel,
                foodDetailViewModel = foodDetailViewModel,
                nutrientEditViewModel = nutrientEditViewModel,
                analyticsViewModel = analyticsViewModel,
                settingsViewModel = settingsViewModel,
                onboardingViewModel = onboardingViewModel,
                customFoodSearchViewModel = customFoodSearchViewModel,
                manualEntryViewModel = manualEntryViewModel,
                authViewModel = authViewModel,
            )
        }
    }
}
