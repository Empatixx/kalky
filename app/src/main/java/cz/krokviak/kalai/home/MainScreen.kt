package cz.krokviak.kalai.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.R
import cz.krokviak.kalai.home.MainUiState
import cz.krokviak.kalai.home.MainViewModel
import cz.krokviak.kalai.home.components.BottomNavBar
import cz.krokviak.kalai.home.components.DonutChart
import cz.krokviak.kalai.home.components.NutrientCard
import cz.krokviak.kalai.home.components.RecentlyAddedList
import kotlin.math.absoluteValue

@Composable
fun MainScreen(
    onCaptureClick: () -> Unit,
    mainViewModel: MainViewModel
) {
    val uiState by mainViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItem = uiState.selectedBottomNavItem,
                onItemSelected = mainViewModel::onBottomNavItemSelected
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
        MyScreenContent(
            modifier = Modifier.padding(innerPadding),
            uiState = uiState
        )
    }
}

fun calorieLabel(calDifference: Int): String {
    val label = if (calDifference > 0) "kcal zbývá" else "kcal přesaženo"
    return label;
}
fun micronutrientLabel(microDiff: Int, microMax: Int): String {
    if (microDiff > 0) {
        return "Zbývá do cíle"
    } else {
        return "Přesah od cíle"
    }
}

@Composable
fun MyScreenContent(
    modifier: Modifier = Modifier,
    uiState: MainUiState
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 0.dp)
    ) {
        // Card: "Calories left" + Donut chart
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
        ) {
            Row(
                modifier = Modifier.padding(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${uiState.calorieDifference().absoluteValue}",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = calorieLabel(uiState.calorieDifference()))
                }

                // Right side: Donut chart
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DonutChart(
                        modifier = Modifier.fillMaxSize(),
                        percentage = uiState.calorieRatio(),
                        activeColor = Color.Black,
                        centerIcon = Icons.Outlined.LocalFireDepartment,
                        centerIconSize = 32.dp,
                        holeRadius = 80f
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Row with three cards: Protein, Carbs, Fat
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NutrientCard(
                amount = "${uiState.proteinDifference().absoluteValue}g",
                aboveDescription = "Bilkoviny",
                belowDescription = micronutrientLabel(uiState.proteinDifference(), uiState.maxProtein),
                iconResId = R.drawable.chicken_leg,
                donutColor = colorResource(id = R.color.proteinColor),
                percentage = uiState.proteinRatio()
            )
            Spacer(modifier = Modifier.width(8.dp))
            NutrientCard(
                amount = "${uiState.carbsDifference().absoluteValue}g",
                aboveDescription = "Sacharidy",
                belowDescription = micronutrientLabel(uiState.carbsDifference(), uiState.maxCarbs),
                iconResId = R.drawable.wheat,
                donutColor = colorResource(id = R.color.carbsColor),
                percentage = uiState.carbsRatio()
            )
            Spacer(modifier = Modifier.width(8.dp))
            NutrientCard(
                amount = "${uiState.fatsDifference().absoluteValue}g",
                aboveDescription = "Tuky",
                belowDescription = micronutrientLabel(uiState.fatsDifference(), uiState.maxFats),
                iconResId = R.drawable.avocado,
                donutColor = colorResource(id = R.color.fatColor),
                percentage = uiState.fatsRatio()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Nedávno přidané",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        RecentlyAddedList(
            items = uiState.recentlyAddedItems,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}
