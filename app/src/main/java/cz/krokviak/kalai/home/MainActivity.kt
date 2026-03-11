package cz.krokviak.kalai.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import cz.krokviak.kalai.analytics.AnalyticsPage
import cz.krokviak.kalai.analytics.AnalyticsViewModel
import cz.krokviak.kalai.camera.CameraActivity
import cz.krokviak.kalai.common.DefaultRoute
import cz.krokviak.kalai.common.FoodDetailRoute
import cz.krokviak.kalai.common.NutrientEditRoute
import cz.krokviak.kalai.detail.FoodDetailScene
import cz.krokviak.kalai.detail.FoodDetailViewModel
import cz.krokviak.kalai.home.components.BottomNavBar
import cz.krokviak.kalai.nutrientedit.NutrientEditScene
import cz.krokviak.kalai.nutrientedit.NutrientEditViewModel
import cz.krokviak.kalai.settings.ProfilePage
import cz.krokviak.kalai.settings.SettingsPage
import cz.krokviak.kalai.settings.SettingsViewModel
import cz.krokviak.kalai.theme.KalaiTheme
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModel()
    private val foodDetailViewModel: FoodDetailViewModel by viewModel()
    private val nutrientEditViewModel: NutrientEditViewModel by viewModel()
    private val analyticsViewModel: AnalyticsViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by viewModel()

    /**
     * Launcher for the camera Activity, handles the result of taking a picture.
     */
    private val cameraResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleCameraResult(result)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KalaiTheme {
                AppContent(
                    mainViewModel = mainViewModel,
                    foodDetailViewModel = foodDetailViewModel,
                    nutrientEditViewModel = nutrientEditViewModel,
                    analyticsViewModel = analyticsViewModel,
                    settingsViewModel = settingsViewModel,
                    cameraResultLauncher = cameraResultLauncher
                )
            }
        }
    }

    /**
     * Handle the result from the camera activity.
     */
    private fun handleCameraResult(result: ActivityResult) {
        if (result.resultCode != RESULT_OK) return

        val data = result.data ?: return
        when (data.getStringExtra(CameraActivity.EXTRA_RESULT_TYPE)) {
            CameraActivity.RESULT_TYPE_PHOTO -> handlePhotoResult(data)
            CameraActivity.RESULT_TYPE_BARCODE -> handleBarcodeResult(data)
        }
    }

    private fun handlePhotoResult(data: Intent) {
        val imageUrl = data.getStringExtra(CameraActivity.EXTRA_IMAGE_URL)
        val imageBytes = imageUrl?.let { File(it).readBytes() }
        if (imageBytes != null) {
            mainViewModel.addFoodItemFromBytes(imageBytes = imageBytes)
        }
    }

    private fun handleBarcodeResult(data: Intent) {
        mainViewModel.addFoodItemFromBarcode(
            name = data.getStringExtra(CameraActivity.EXTRA_NAME) ?: "Neznámý produkt",
            calories = data.getIntExtra(CameraActivity.EXTRA_CALORIES, 0),
            protein = data.getIntExtra(CameraActivity.EXTRA_PROTEIN, 0),
            fat = data.getIntExtra(CameraActivity.EXTRA_FAT, 0),
            carbs = data.getIntExtra(CameraActivity.EXTRA_CARBS, 0)
        )
    }
}

/**
 * Top-level content of the app which sets up the NavController and the NavHost.
 */
@Composable
fun AppContent(
    mainViewModel: MainViewModel,
    foodDetailViewModel: FoodDetailViewModel,
    nutrientEditViewModel: NutrientEditViewModel,
    analyticsViewModel: AnalyticsViewModel,
    settingsViewModel: SettingsViewModel,
    cameraResultLauncher: ActivityResultLauncher<Intent>
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = DefaultRoute
    ) {
        /**
         * The default (home) composable which uses our main scaffold layout.
         */
        composable<DefaultRoute> {
            MainScaffold(
                mainViewModel = mainViewModel,
                analyticsViewModel = analyticsViewModel,
                settingsViewModel = settingsViewModel,
                onCameraClick = {
                    cameraResultLauncher.launch(
                        Intent(context, CameraActivity::class.java)
                    )
                },
                navController = navController
            )
        }

        /**
         * Food Detail route.
         */
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
                onShareClick = { /* TODO */ }
            )
        }

        /**
         * Nutrient Edit route.
         */
        composable<NutrientEditRoute> { backStackEntry ->
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
    }
}

/**
 * This Composable sets up the Scaffold that contains a pager and bottom navigation.
 */
@Composable
fun MainScaffold(
    mainViewModel: MainViewModel,
    analyticsViewModel: AnalyticsViewModel,
    settingsViewModel: SettingsViewModel,
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
                    modifier = Modifier.fillMaxSize()
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
                        navController = navController,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                3 -> SettingsPage(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
