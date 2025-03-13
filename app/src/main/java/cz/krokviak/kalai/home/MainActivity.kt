package cz.krokviak.kalai.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.compose.material3.MaterialTheme
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
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val foodDetailViewModel: FoodDetailViewModel by viewModels()
    private val nutrientEditViewModel: NutrientEditViewModel by viewModels()

    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 2) Check if the result was OK
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val imageUrl = data?.getStringExtra("imageUrl")
                val file = imageUrl?.let { File(it) }
                val imageBytes = file?.readBytes()
                if (imageBytes != null) {
                    // Pass them to the ViewModel to handle
                    mainViewModel.addFoodItemFromBytes(context = this.application, imageBytes = imageBytes)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                NavHost(
                    navController = navController,
                    startDestination = DefaultRoute
                ) {
                    composable<DefaultRoute> {
                        // Your original scaffold and pager
                        Content(
                            onCaptureClick = {
                                // 1) Start the camera activity
                                cameraResultLauncher.launch(Intent(context, CameraActivity::class.java))
                            },
                            mainViewModel = mainViewModel,
                            navController
                        )
                    }
                    composable<FoodDetailRoute> { backStackEntry ->
                        val food: FoodDetailRoute = backStackEntry.toRoute()
                        val uiState by foodDetailViewModel.uiState.collectAsState()
                        val context = LocalContext.current
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
                    composable< NutrientEditRoute> { backStackEntry ->
                        val uiState by nutrientEditViewModel.uiState.collectAsState()
                        NutrientEditScene (
                            nutrientEditViewModel = nutrientEditViewModel,
                            uiState = uiState,
                            onBackClick = { navController.popBackStack() },
                            onNutrientEdit = { nutrientEditState ->
                                mainViewModel.updateTargetNutrients(nutrientEditState)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun Content(
    onCaptureClick: () -> Unit,
    mainViewModel: MainViewModel,
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
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                ),
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