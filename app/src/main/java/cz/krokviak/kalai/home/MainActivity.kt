package cz.krokviak.kalai.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import cz.krokviak.kalai.camera.CameraActivity
import cz.krokviak.kalai.common.DefaultRoute
import cz.krokviak.kalai.common.FoodDetailRoute
import cz.krokviak.kalai.common.NutrientEditRoute
import cz.krokviak.kalai.detail.FoodDetailScene
import cz.krokviak.kalai.detail.FoodDetailViewModel
import cz.krokviak.kalai.home.components.BottomNavBar
import cz.krokviak.kalai.nutrientedit.NutrientEditScene
import cz.krokviak.kalai.nutrientedit.NutrientEditViewModel
import io.github.alexzhirkevich.cupertino.theme.CupertinoTheme
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModel()
    private val foodDetailViewModel: FoodDetailViewModel by viewModel()
    private val nutrientEditViewModel: NutrientEditViewModel by viewModel()

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
            CupertinoTheme {
                AppContent(
                    mainViewModel = mainViewModel,
                    foodDetailViewModel = foodDetailViewModel,
                    nutrientEditViewModel = nutrientEditViewModel,
                    cameraResultLauncher = cameraResultLauncher
                )
            }
        }
    }

    /**
     * Handle the result from the camera activity.
     */
    private fun handleCameraResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val imageUrl = data?.getStringExtra("imageUrl")
            val file = imageUrl?.let { File(it) }
            val imageBytes = file?.readBytes()
            if (imageBytes != null) {
                mainViewModel.addFoodItemFromBytes(
                    context = application,
                    imageBytes = imageBytes
                )
            }
        }
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
                onCaptureClick = {
                    // Launch the camera Activity.
                    cameraResultLauncher.launch(
                        Intent(
                            context,
                            CameraActivity::class.java
                        )
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
                foodDetailViewModel.loadFood(context, food.id)
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
    onCaptureClick: () -> Unit,
    navController: NavController
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 3 }
    )
    val scope = rememberCoroutineScope()
    val uiState by mainViewModel.uiState.collectAsState()
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
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCaptureClick,
                containerColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier
                    .offset(y = 48.dp)
                    .size(72.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        HorizontalPager(
            beyondViewportPageCount = 3,
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
                    uiState = uiState,
                    mainViewModel = mainViewModel,
                    modifier = Modifier.fillMaxSize()
                )
                2 -> Text(
                    text = "Settings",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
