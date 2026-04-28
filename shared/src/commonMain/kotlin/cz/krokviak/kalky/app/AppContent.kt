package cz.krokviak.kalky.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import cz.krokviak.kalky.analytics.AnalyticsViewModel
import cz.krokviak.kalky.auth.AuthStateProvider
import cz.krokviak.kalky.auth.AuthViewModelInterface
import cz.krokviak.kalky.common.AppPreferences
import cz.krokviak.kalky.common.domain.CompleteOnboardingUseCase
import cz.krokviak.kalky.common.CustomFoodRoute
import cz.krokviak.kalky.common.DefaultRoute
import cz.krokviak.kalky.common.FoodDetailRoute
import cz.krokviak.kalky.common.LocalPlatformActions
import cz.krokviak.kalky.common.LoginRoute
import cz.krokviak.kalky.common.ManualFoodEntryRoute
import cz.krokviak.kalky.common.NutrientEditRoute
import cz.krokviak.kalky.common.OnboardingRoute
import cz.krokviak.kalky.common.PrivacyPolicyRoute
import cz.krokviak.kalky.common.TermsRoute
import cz.krokviak.kalky.customfood.CustomFoodSearchViewModel
import cz.krokviak.kalky.customfood.ManualFoodEntryViewModel
import cz.krokviak.kalky.detail.FoodDetailViewModel
import cz.krokviak.kalky.di.koinInject
import cz.krokviak.kalky.home.MainViewModel
import cz.krokviak.kalky.nutrientedit.NutrientEditViewModel
import cz.krokviak.kalky.onboarding.OnboardingViewModel
import cz.krokviak.kalky.settings.PrivacyPolicyScene
import cz.krokviak.kalky.settings.SettingsViewModel
import cz.krokviak.kalky.settings.TermsScene
import cz.krokviak.kalky.ui.components.ResponsiveProvider

@Composable
fun AppContent(
    mainViewModel: MainViewModel,
    foodDetailViewModel: FoodDetailViewModel,
    nutrientEditViewModel: NutrientEditViewModel,
    analyticsViewModel: AnalyticsViewModel,
    settingsViewModel: SettingsViewModel,
    onboardingViewModel: OnboardingViewModel,
    customFoodSearchViewModel: CustomFoodSearchViewModel,
    manualEntryViewModel: ManualFoodEntryViewModel,
    authViewModel: AuthViewModelInterface,
) {
    val navController = rememberNavController()
    val platformActions = LocalPlatformActions.current
    val appPreferences: AppPreferences = koinInject()
    val authStateProvider: AuthStateProvider = koinInject()
    val completeOnboarding: CompleteOnboardingUseCase = koinInject()

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
                OnboardingDestination(onboardingViewModel = onboardingViewModel)
            }

            composable<LoginRoute> {
                LoginDestination(
                    authViewModel = authViewModel,
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
                    mainViewModel = mainViewModel,
                    analyticsViewModel = analyticsViewModel,
                    settingsViewModel = settingsViewModel,
                    manualEntryViewModel = manualEntryViewModel,
                    authViewModel = authViewModel,
                    onCameraClick = { platformActions.launchCamera() },
                    navController = navController
                )
            }

            composable<FoodDetailRoute> { backStackEntry ->
                val food: FoodDetailRoute = backStackEntry.toRoute()
                FoodDetailDestination(
                    foodDetailViewModel = foodDetailViewModel,
                    foodId = food.id,
                    onExit = { navController.popBackStack() },
                    onShare = { path -> platformActions.shareImage(path) },
                    onDelete = { navController.popBackStack() }
                )
            }

            composable<NutrientEditRoute> {
                NutrientEditDestination(
                    nutrientEditViewModel = nutrientEditViewModel,
                    mainViewModel = mainViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<CustomFoodRoute> {
                CustomFoodDestination(
                    searchViewModel = customFoodSearchViewModel,
                    manualEntryViewModel = manualEntryViewModel,
                    onBack = { navController.popBackStack() },
                    onAddNew = { navController.navigate(ManualFoodEntryRoute) },
                    onFoodAdded = {
                        mainViewModel.loadFoodItemsForDate(mainViewModel.uiState.value.currentDate)
                        navController.popBackStack()
                    }
                )
            }

            composable<ManualFoodEntryRoute> {
                ManualFoodEntryDestination(
                    manualEntryViewModel = manualEntryViewModel,
                    onBack = { navController.popBackStack() },
                    onFoodAdded = {
                        mainViewModel.loadFoodItemsForDate(mainViewModel.uiState.value.currentDate)
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
