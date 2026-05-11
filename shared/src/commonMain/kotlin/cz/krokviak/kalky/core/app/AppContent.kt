package cz.krokviak.kalky.core.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import cz.krokviak.kalky.scenes.auth.AuthStateProvider
import cz.krokviak.kalky.scenes.auth.AuthViewModelInterface
import cz.krokviak.kalky.core.common.AppPreferences
import cz.krokviak.kalky.core.common.domain.CompleteOnboardingUseCase
import cz.krokviak.kalky.core.common.CustomFoodRoute
import cz.krokviak.kalky.core.common.DefaultRoute
import cz.krokviak.kalky.core.common.FoodDetailRoute
import cz.krokviak.kalky.core.common.LocalPlatformActions
import cz.krokviak.kalky.core.common.LoginRoute
import cz.krokviak.kalky.core.common.ManualFoodEntryRoute
import cz.krokviak.kalky.core.common.NutrientEditRoute
import cz.krokviak.kalky.core.common.OnboardingRoute
import cz.krokviak.kalky.core.common.PrivacyPolicyRoute
import cz.krokviak.kalky.core.common.TermsRoute
import cz.krokviak.kalky.core.di.koinInject
import org.koin.compose.viewmodel.koinViewModel
import cz.krokviak.kalky.scenes.home.MainViewModel
import cz.krokviak.kalky.scenes.onboarding.OnboardingViewModel
import cz.krokviak.kalky.scenes.settings.PrivacyPolicyScene
import cz.krokviak.kalky.scenes.settings.SettingsViewModel
import cz.krokviak.kalky.scenes.settings.TermsScene
import cz.krokviak.kalky.core.ui.components.ResponsiveProvider

@Composable
fun AppContent() {
    val navController = rememberNavController()
    val platformActions = LocalPlatformActions.current
    val appPreferences: AppPreferences = koinInject()
    val authStateProvider: AuthStateProvider = koinInject()
    val completeOnboarding: CompleteOnboardingUseCase = koinInject()
    val mainViewModel: MainViewModel = koinViewModel()
    val onboardingViewModel: OnboardingViewModel = koinViewModel()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    // AuthViewModelInterface is a plain interface (not a ViewModel subtype), so
    // koinViewModel<T>() would reify T to the common supertype `Any` and fail to
    // resolve. Resolve it through the regular Koin scope instead.
    val authViewModel: AuthViewModelInterface = koinInject()

    val onboardingCompleted by appPreferences.onboardingCompleted.collectAsState()
    val isAuthenticated by authStateProvider.isAuthenticated.collectAsState()

    val startDestination: Any = when {
        !onboardingCompleted -> OnboardingRoute
        !isAuthenticated -> LoginRoute
        else -> DefaultRoute
    }

    ResponsiveProvider {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable<OnboardingRoute> {
                LaunchedEffect(onboardingViewModel) {
                    onboardingViewModel.completed.collect { result ->
                        completeOnboarding(result)
                        mainViewModel.refreshNutrientSettings()
                        settingsViewModel.refresh()
                        appPreferences.setOnboardingCompleted(true)
                        navController.navigate(LoginRoute) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
                OnboardingDestination()
            }

            composable<LoginRoute> {
                LoginDestination(
                    onSignInWithGoogle = { platformActions.signInWithGoogle() },
                    onSignInWithApple = { platformActions.signInWithApple() },
                    onSignInSuccess = {
                        navController.navigate(DefaultRoute) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable<DefaultRoute> {
                MainScaffold(
                    onCameraClick = { platformActions.launchCamera() },
                    navController = navController
                )
            }

            composable<FoodDetailRoute> { backStackEntry ->
                val food: FoodDetailRoute = backStackEntry.toRoute()
                FoodDetailDestination(
                    foodId = food.id,
                    onExit = { navController.popBackStack() },
                    onShare = { path -> platformActions.shareImage(path) },
                    onDelete = { navController.popBackStack() }
                )
            }

            composable<NutrientEditRoute> {
                NutrientEditDestination(
                    onBack = { navController.popBackStack() }
                )
            }

            composable<CustomFoodRoute> {
                CustomFoodDestination(
                    onBack = { navController.popBackStack() },
                    onAddNew = { navController.navigate(ManualFoodEntryRoute) },
                    onFoodAdded = {
                        // MainViewModel's daily-macros flow re-emits automatically
                        // after the insert; no explicit reload needed.
                        navController.popBackStack()
                    }
                )
            }

            composable<ManualFoodEntryRoute> {
                ManualFoodEntryDestination(
                    onBack = { navController.popBackStack() },
                    onFoodAdded = {
                        navController.navigate(DefaultRoute) {
                            popUpTo(DefaultRoute) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable<TermsRoute> {
                TermsScene(onBackClick = { navController.popBackStack() })
            }

            composable<PrivacyPolicyRoute> {
                PrivacyPolicyScene(onBackClick = { navController.popBackStack() })
            }
        }
    }
}
