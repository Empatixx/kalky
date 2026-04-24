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
import cz.krokviak.kalky.analytics.AnalyticsPage
import cz.krokviak.kalky.analytics.AnalyticsViewModel
import cz.krokviak.kalky.auth.AuthViewModelInterface
import cz.krokviak.kalky.auth.LoginPage
import cz.krokviak.kalky.common.AppPreferences
import cz.krokviak.kalky.common.CustomFoodRoute
import cz.krokviak.kalky.common.DefaultRoute
import cz.krokviak.kalky.common.FoodDetailRoute
import cz.krokviak.kalky.common.LoginRoute
import cz.krokviak.kalky.common.ManualFoodEntryRoute
import cz.krokviak.kalky.common.NutrientEditRoute
import cz.krokviak.kalky.common.TermsRoute
import cz.krokviak.kalky.common.PrivacyPolicyRoute
import cz.krokviak.kalky.customfood.CustomFoodScene
import cz.krokviak.kalky.customfood.CustomFoodViewModel
import cz.krokviak.kalky.customfood.ManualFoodEntryScene
import cz.krokviak.kalky.detail.FoodDetailScene
import cz.krokviak.kalky.detail.FoodDetailViewModel
import cz.krokviak.kalky.home.HomeScene
import cz.krokviak.kalky.home.MainViewModel
import cz.krokviak.kalky.home.components.BottomNavBar
import cz.krokviak.kalky.nutrientedit.NutrientEditScene
import cz.krokviak.kalky.nutrientedit.NutrientEditViewModel
import cz.krokviak.kalky.onboarding.OnboardingPage
import cz.krokviak.kalky.onboarding.OnboardingResult
import cz.krokviak.kalky.onboarding.OnboardingViewModel
import cz.krokviak.kalky.settings.ProfilePage
import cz.krokviak.kalky.settings.SettingsPage
import cz.krokviak.kalky.settings.SettingsViewModel
import cz.krokviak.kalky.ui.components.KalkyGradientBackground
import kotlinx.coroutines.launch

@Composable
internal fun OnboardingDestination(
    onboardingViewModel: OnboardingViewModel,
    onFinish: (OnboardingResult) -> Unit,
) {
    OnboardingPage(
        onboardingViewModel = onboardingViewModel,
        onFinish = onFinish
    )
}

@Composable
internal fun LoginDestination(
    authViewModel: AuthViewModelInterface,
    onSignInWithGoogle: () -> Unit,
    onSignInWithApple: () -> Unit,
    onSignInSuccess: () -> Unit,
) {
    val uiState by authViewModel.uiState.collectAsState()
    LoginPage(
        onSignInWithGoogle = onSignInWithGoogle,
        onSignInWithApple = onSignInWithApple,
        onSignInSuccess = onSignInSuccess,
        isLoading = uiState.isLoading,
        error = uiState.error,
        isSignedIn = uiState.isSignedIn
    )
}

@Composable
internal fun FoodDetailDestination(
    foodDetailViewModel: FoodDetailViewModel,
    foodId: Long,
    onExit: () -> Unit,
    onShare: (String) -> Unit,
    onDelete: () -> Unit,
) {
    val uiState by foodDetailViewModel.uiState.collectAsState()
    LaunchedEffect(foodId) { foodDetailViewModel.loadFood(foodId) }
    FoodDetailScene(
        foodDetailViewModel = foodDetailViewModel,
        uiState = uiState,
        foodId = foodId,
        onExitClick = onExit,
        onShareClick = {
            val imagePath = uiState.localImagePath
            if (imagePath != null) onShare(imagePath)
        },
        onDeleteClick = {
            foodDetailViewModel.deleteFood()
            onDelete()
        }
    )
}

@Composable
internal fun NutrientEditDestination(
    nutrientEditViewModel: NutrientEditViewModel,
    mainViewModel: MainViewModel,
    onBack: () -> Unit,
) {
    val uiState by nutrientEditViewModel.uiState.collectAsState()
    NutrientEditScene(
        nutrientEditViewModel = nutrientEditViewModel,
        uiState = uiState,
        onBackClick = {
            mainViewModel.updateNutrientSettings(
                uiState.protein,
                uiState.carbs,
                uiState.fat,
                uiState.calories
            )
            onBack()
        }
    )
}

@Composable
internal fun CustomFoodDestination(
    customFoodViewModel: CustomFoodViewModel,
    onBack: () -> Unit,
    onAddNew: () -> Unit,
    onFoodAdded: () -> Unit,
) {
    val uiState by customFoodViewModel.uiState.collectAsState()
    CustomFoodScene(
        uiState = uiState,
        foodAdded = customFoodViewModel.foodAdded,
        onBackClick = onBack,
        onAddNewClick = {
            customFoodViewModel.resetManualEntry()
            onAddNew()
        },
        onFoodAdded = onFoodAdded,
        onLoadHistory = customFoodViewModel::loadHistory,
        onSearchQueryChange = customFoodViewModel::onSearchQueryChange,
        onToggleSelection = customFoodViewModel::toggleSelection,
        onSelectApiProduct = customFoodViewModel::selectApiProduct,
        onAddSelectedFoods = customFoodViewModel::addSelectedFoods,
        onPortionChanged = customFoodViewModel::setPortionGrams,
        onConfirmApiProduct = customFoodViewModel::confirmAddApiProduct,
        onDismissPortionPicker = customFoodViewModel::dismissPortionPicker,
    )
}

@Composable
internal fun ManualFoodEntryDestination(
    customFoodViewModel: CustomFoodViewModel,
    onBack: () -> Unit,
    onFoodAdded: () -> Unit,
) {
    ManualFoodEntryScene(
        viewModel = customFoodViewModel,
        onBackClick = onBack,
        onFoodAdded = onFoodAdded
    )
}

@Composable
internal fun MainScaffold(
    mainViewModel: MainViewModel,
    analyticsViewModel: AnalyticsViewModel,
    settingsViewModel: SettingsViewModel,
    customFoodViewModel: CustomFoodViewModel,
    authViewModel: AuthViewModelInterface,
    onCameraClick: () -> Unit,
    navController: NavController,
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 4 }
    )
    val scope = rememberCoroutineScope()
    val uiState by mainViewModel.uiState.collectAsState()

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
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { page ->
                when (page) {
                    0 -> HomePage(
                        uiState = uiState,
                        mainViewModel = mainViewModel,
                        customFoodViewModel = customFoodViewModel,
                        navController = navController,
                    )
                    1 -> AnalyticsPageDestination(analyticsViewModel = analyticsViewModel)
                    2 -> {
                        val settingsUiState by settingsViewModel.uiState.collectAsState()
                        ProfilePage(
                            uiState = settingsUiState,
                            viewModel = settingsViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    3 -> AccountPage(
                        authViewModel = authViewModel,
                        navController = navController,
                    )
                }
            }
        }
    }
}

@Composable
private fun HomePage(
    uiState: cz.krokviak.kalky.home.MainUiState,
    mainViewModel: MainViewModel,
    customFoodViewModel: CustomFoodViewModel,
    navController: NavController,
) {
    HomeScene(
        uiState = uiState,
        modifier = Modifier.fillMaxSize(),
        onFoodClick = { id ->
            if (uiState.isSelectionMode) {
                mainViewModel.toggleFoodSelection(id)
            } else {
                navController.navigate(FoodDetailRoute(id))
            }
        },
        onFoodLongClick = { id -> mainViewModel.toggleFoodSelection(id) },
        onDateChange = mainViewModel::onDateSelected,
        onTodayClick = mainViewModel::resetToToday,
        onAddCustomClick = { navController.navigate(CustomFoodRoute) },
        onEditTargetsClick = { navController.navigate(NutrientEditRoute) },
        onSelectionClear = mainViewModel::clearSelection,
        onSaveSelectionAsCustom = {
            val items = mainViewModel.getSelectedFoodItems()
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
            mainViewModel.clearSelection()
            navController.navigate(ManualFoodEntryRoute)
        },
        onDeleteSelection = mainViewModel::deleteSelectedFoods,
    )
}

@Composable
private fun AnalyticsPageDestination(analyticsViewModel: AnalyticsViewModel) {
    val analyticsUiState by analyticsViewModel.uiState.collectAsState()
    AnalyticsPage(
        uiState = analyticsUiState,
        analyticsViewModel = analyticsViewModel,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun AccountPage(
    authViewModel: AuthViewModelInterface,
    navController: NavController,
) {
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
