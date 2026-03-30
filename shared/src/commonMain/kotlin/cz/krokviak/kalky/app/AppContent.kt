package cz.krokviak.kalky.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import cz.krokviak.kalky.analytics.AnalyticsPage
import cz.krokviak.kalky.analytics.AnalyticsViewModel
import cz.krokviak.kalky.auth.AuthStateProvider
import cz.krokviak.kalky.auth.AuthUiState
import cz.krokviak.kalky.auth.AuthViewModelInterface
import cz.krokviak.kalky.auth.LoginPage
import cz.krokviak.kalky.common.AppPreferences
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
import cz.krokviak.kalky.customfood.CustomFoodScene
import cz.krokviak.kalky.customfood.CustomFoodViewModel
import cz.krokviak.kalky.customfood.ManualFoodEntryScene
import cz.krokviak.kalky.detail.FoodDetailScene
import cz.krokviak.kalky.detail.FoodDetailViewModel
import cz.krokviak.kalky.di.koinInject
import cz.krokviak.kalky.home.HomeScene
import cz.krokviak.kalky.home.MainViewModel
import cz.krokviak.kalky.home.components.BottomNavBar
import cz.krokviak.kalky.nutrientedit.NutrientEditScene
import cz.krokviak.kalky.nutrientedit.NutrientEditViewModel
import cz.krokviak.kalky.onboarding.OnboardingPage
import cz.krokviak.kalky.onboarding.OnboardingViewModel
import cz.krokviak.kalky.settings.ProfilePage
import cz.krokviak.kalky.settings.PrivacyPolicyPage
import cz.krokviak.kalky.settings.SettingsPage
import cz.krokviak.kalky.settings.SettingsViewModel
import cz.krokviak.kalky.settings.TermsPage
import cz.krokviak.kalky.ui.components.KalkyGradientBackground
import cz.krokviak.kalky.ui.components.ResponsiveProvider
import kotlinx.coroutines.launch

@Composable
fun AppContent(
    mainViewModel: MainViewModel,
    foodDetailViewModel: FoodDetailViewModel,
    nutrientEditViewModel: NutrientEditViewModel,
    analyticsViewModel: AnalyticsViewModel,
    settingsViewModel: SettingsViewModel,
    onboardingViewModel: OnboardingViewModel,
    customFoodViewModel: CustomFoodViewModel,
    authViewModel: AuthViewModelInterface,
) {
    val navController = rememberNavController()
    val platformActions = LocalPlatformActions.current
    val appPreferences: AppPreferences = koinInject()
    val authStateProvider: AuthStateProvider = koinInject()

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
                OnboardingPage(
                    onboardingViewModel = onboardingViewModel,
                    onFinish = { result ->
                        settingsViewModel.onGenderChange(result.gender)
                        settingsViewModel.onWeightChange(result.weight)
                        settingsViewModel.onHeightChange(result.height)
                        settingsViewModel.onAgeChange(result.age)
                        settingsViewModel.onActivityLevelChange(result.activityLevel)
                        settingsViewModel.save()
                        if (result.targetCalories > 0) {
                            nutrientEditViewModel.onProteinChange(result.targetProtein)
                            nutrientEditViewModel.onCarbsChange(result.targetCarbs)
                            nutrientEditViewModel.onFatChange(result.targetFat)
                            mainViewModel.updateNutrientSettings(
                                result.targetProtein,
                                result.targetCarbs,
                                result.targetFat,
                                result.targetCalories
                            )
                        }
                        appPreferences.setOnboardingCompleted(true)
                        navController.navigate(LoginRoute) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable<LoginRoute> {
                val uiState by authViewModel.uiState.collectAsState()
                LoginPage(
                    onSignInWithGoogle = { platformActions.signInWithGoogle() },
                    onSignInWithApple = { platformActions.signInWithApple() },
                    onSignInSuccess = {
                        navController.navigate(DefaultRoute) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    isLoading = uiState.isLoading,
                    error = uiState.error,
                    isSignedIn = uiState.isSignedIn
                )
            }

            composable<DefaultRoute> {
                MainScaffold(
                    mainViewModel = mainViewModel,
                    analyticsViewModel = analyticsViewModel,
                    settingsViewModel = settingsViewModel,
                    customFoodViewModel = customFoodViewModel,
                    authViewModel = authViewModel,
                    onCameraClick = { platformActions.launchCamera() },
                    navController = navController
                )
            }

            composable<FoodDetailRoute> { backStackEntry ->
                val food: FoodDetailRoute = backStackEntry.toRoute()
                val uiState by foodDetailViewModel.uiState.collectAsState()
                LaunchedEffect(food.id) {
                    foodDetailViewModel.loadFood(food.id)
                }
                FoodDetailScene(
                    foodDetailViewModel = foodDetailViewModel,
                    uiState = uiState,
                    foodId = food.id,
                    onExitClick = { navController.popBackStack() },
                    onShareClick = {
                        val imagePath = uiState.localImagePath
                        if (imagePath != null) {
                            platformActions.shareImage(imagePath)
                        }
                    },
                    onDeleteClick = {
                        foodDetailViewModel.deleteFood()
                        navController.popBackStack()
                    }
                )
            }

            composable<NutrientEditRoute> {
                val uiState by nutrientEditViewModel.uiState.collectAsState()
                NutrientEditScene(
                    nutrientEditViewModel = nutrientEditViewModel,
                    uiState = uiState,
                    onBackClick = {
                        navController.popBackStack()
                        mainViewModel.updateNutrientSettings(
                            uiState.protein,
                            uiState.carbs,
                            uiState.fat,
                            uiState.calories
                        )
                    }
                )
            }

            composable<CustomFoodRoute> {
                CustomFoodScene(
                    viewModel = customFoodViewModel,
                    onBackClick = { navController.popBackStack() },
                    onAddNewClick = {
                        customFoodViewModel.resetManualEntry()
                        navController.navigate(ManualFoodEntryRoute)
                    },
                    onFoodAdded = {
                        mainViewModel.loadFoodItemsForDate(mainViewModel.uiState.value.currentDate)
                        navController.popBackStack()
                    }
                )
            }

            composable<ManualFoodEntryRoute> {
                ManualFoodEntryScene(
                    viewModel = customFoodViewModel,
                    onBackClick = { navController.popBackStack() },
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
                TermsPage(onBackClick = { navController.popBackStack() })
            }

            composable<PrivacyPolicyRoute> {
                PrivacyPolicyPage(onBackClick = { navController.popBackStack() })
            }
        }
    }
}

@Composable
fun MainScaffold(
    mainViewModel: MainViewModel,
    analyticsViewModel: AnalyticsViewModel,
    settingsViewModel: SettingsViewModel,
    customFoodViewModel: CustomFoodViewModel,
    authViewModel: AuthViewModelInterface,
    onCameraClick: () -> Unit,
    navController: NavController
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 4 }
    )
    val scope = rememberCoroutineScope()
    val uiState by mainViewModel.uiState.collectAsState()
    val analyticsUiState by analyticsViewModel.uiState.collectAsState()

    val currentPage by remember { derivedStateOf { pagerState.currentPage } }

    LaunchedEffect(Unit) {
        mainViewModel.loadFoodItemsForDate(uiState.currentDate)
    }

    KalkyGradientBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomNavBar(
                    currentPage = currentPage,
                    onSceneSelected = { page ->
                        scope.launch { pagerState.animateScrollToPage(page) }
                    },
                    onCameraClick = onCameraClick
                )
            },
        ) { innerPadding ->
            HorizontalPager(
                beyondViewportPageCount = 4,
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { page ->
                when (page) {
                    0 -> HomeScene(
                        uiState = uiState,
                        model = mainViewModel,
                        navController = navController,
                        modifier = Modifier.fillMaxSize(),
                        onSaveAsCustom = { items ->
                            val totalProtein = items.sumOf { it.protein }
                            val totalCarbs = items.sumOf { it.carbs }
                            val totalFat = items.sumOf { it.fat }
                            val name = items.joinToString(" + ") { it.name }
                            customFoodViewModel.resetManualEntry()
                            customFoodViewModel.onNameChange(name)
                            customFoodViewModel.onManualProteinChange(totalProtein)
                            customFoodViewModel.onManualCarbsChange(totalCarbs)
                            customFoodViewModel.onManualFatChange(totalFat)
                            customFoodViewModel.setSourceFoods(items)
                            navController.navigate(ManualFoodEntryRoute)
                        }
                    )
                    1 -> AnalyticsPage(
                        uiState = analyticsUiState,
                        analyticsViewModel = analyticsViewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                    2 -> {
                        val settingsUiState by settingsViewModel.uiState.collectAsState()
                        ProfilePage(
                            uiState = settingsUiState,
                            viewModel = settingsViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    3 -> {
                        val authUser by authViewModel.authUser.collectAsState()
                        SettingsPage(
                            modifier = Modifier.fillMaxSize(),
                            authUser = authUser,
                            onTermsClick = { navController.navigate(TermsRoute) },
                            onPrivacyClick = { navController.navigate(PrivacyPolicyRoute) },
                            onSignOutClick = {
                                authViewModel.signOut()
                                navController.navigate(LoginRoute) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
