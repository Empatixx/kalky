package cz.krokviak.kalai.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.R
import cz.krokviak.kalai.home.AnalyticsScene
import cz.krokviak.kalai.home.MainUiState
import cz.krokviak.kalai.home.MainViewModel
import cz.krokviak.kalai.home.Scene
import cz.krokviak.kalai.home.components.BottomNavBar
import cz.krokviak.kalai.home.components.CalorieCard
import cz.krokviak.kalai.home.components.FoodItemCard
import cz.krokviak.kalai.home.components.MacroNutrientCard
import cz.krokviak.kalai.home.components.WeekDatePicker
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.section.CupertinoSection
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun MainScreen(
    onCaptureClick: () -> Unit,
    mainViewModel: MainViewModel
) {
    // Scenes in a list, so we can map indices <-> Scene
    val scenes = remember { listOf(Scene.HOME, Scene.ANALYTICS, Scene.SETTINGS) }

    // Pager state + coroutine scope for animating
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { scenes.size }
    )
    val scope = rememberCoroutineScope()

    // Optionally collect UI state from your ViewModel if needed
    val uiState by mainViewModel.uiState.collectAsState()

    // Keep track of the "current scene" based on the pager’s current page
    val currentScene by remember {
        derivedStateOf {
            scenes[pagerState.currentPage]
        }
    }

    Scaffold(
        containerColor = Color.Transparent,  // Make the scaffold background transparent
        bottomBar = {
            BottomNavBar(
                currentScene = currentScene,
                onSceneSelected = { scene ->
                    // Animate pager to the correct scene
                    val pageIndex = scenes.indexOf(scene)
                    scope.launch {
                        pagerState.animateScrollToPage(pageIndex)
                    }
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

        // HorizontalPager from foundation.pager
        HorizontalPager(
            beyondViewportPageCount = 3,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            // Each page will render a composable corresponding to the scene
            when (val scene = scenes[page]) {
                Scene.HOME -> {
                    MyScreenContent(
                        uiState = uiState,
                        model = mainViewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Scene.SETTINGS -> {
                    Text(
                        text = "Settings",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Scene.ANALYTICS -> {
                    AnalyticsScene(mainViewModel)
                }
            }
        }
    }
}

fun micronutrientLabel(microDiff: Int, microMax: Int): String {
    if (microDiff > 0) {
        return "zbývá do cíle"
    } else {
        return "přesah od cíle"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreenContent(
    modifier: Modifier = Modifier,
    uiState: MainUiState,
    model: MainViewModel
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {

        item {
            WeekDatePicker(
                currentDate = uiState.currentDate,
                onDateChange = model::onDateSelected
            )

            CalorieCard(uiState)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroNutrientCard(
                    amount = "${uiState.proteinDifference().absoluteValue}g",
                    aboveDescription = "Bilkoviny",
                    belowDescription = micronutrientLabel(
                        uiState.proteinDifference(),
                        uiState.maxProtein
                    ),
                    iconResId = R.drawable.chicken_leg,
                    donutColor = colorResource(id = R.color.proteinColor),
                    percentage = uiState.proteinRatio()
                )
                Spacer(modifier = Modifier.width(8.dp))
                MacroNutrientCard(
                    amount = "${uiState.carbsDifference().absoluteValue}g",
                    aboveDescription = "Sacharidy",
                    belowDescription = micronutrientLabel(
                        uiState.carbsDifference(),
                        uiState.maxCarbs
                    ),
                    iconResId = R.drawable.wheat,
                    donutColor = colorResource(id = R.color.carbsColor),
                    percentage = uiState.carbsRatio()
                )
                Spacer(modifier = Modifier.width(8.dp))
                MacroNutrientCard(
                    amount = "${uiState.fatsDifference().absoluteValue}g",
                    aboveDescription = "Tuky",
                    belowDescription = micronutrientLabel(
                        uiState.fatsDifference(),
                        uiState.maxFats
                    ),
                    iconResId = R.drawable.avocado,
                    donutColor = colorResource(id = R.color.fatColor),
                    percentage = uiState.fatsRatio()
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            CupertinoText(
                text = "Přidáno dnes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            if (uiState.recentlyAddedItems.isEmpty()) {
                EmptyRecentlyAddedList()
            }
        }
        items(uiState.recentlyAddedItems, key = {
            it.id
        }) { item ->
            FoodItemCard(item, uiState.loadingProgressForItems[item.id] ?: 0)
        }

    }

}

@Composable
fun EmptyRecentlyAddedList() {
    CupertinoSection(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CupertinoText(
                text = "Dneska jsi ještě nic nepřidal/a",
                style = MaterialTheme.typography.titleMedium
            )
            CupertinoText(
                text = "Klikni na tlačítko dole a přidej si první jídlo",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
