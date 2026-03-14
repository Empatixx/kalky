package cz.krokviak.kalai.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cz.krokviak.kalai.R
import cz.krokviak.kalai.common.CustomFoodRoute
import cz.krokviak.kalai.common.FoodDetailRoute
import cz.krokviak.kalai.common.NutrientEditRoute
import cz.krokviak.kalai.home.components.CalorieCard
import cz.krokviak.kalai.home.components.FoodItemCard
import cz.krokviak.kalai.home.components.MacroNutrientCard
import cz.krokviak.kalai.home.components.WeekDatePicker
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.i18n.LocalStrings
import cz.krokviak.kalai.ui.components.KalaiCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScene(
    modifier: Modifier = Modifier,
    uiState: MainUiState,
    navController: NavController,
    model: MainViewModel
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            val s = LocalStrings.current
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                WeekDatePicker(
                    currentDate = uiState.currentDate,
                    isToday = uiState.isToday,
                    onDateChange = model::onDateSelected,
                    onTodayClick = model::resetToToday
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            navController.navigate(NutrientEditRoute)
                        }
                ) {
                    CalorieCard(
                        uiState.currentCalories,
                        uiState.maxCalories,
                        uiState.calorieRatio(),
                        currentStreak = uiState.currentStreak,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MacroNutrientCard(
                            amount = "${uiState.currentProtein}g",
                            maxAmount = "${uiState.maxProtein}g",
                            title = s.common.protein,
                            iconResId = R.drawable.chicken_leg,
                            donutColor = colorResource(id = R.color.proteinColor),
                            percentage = uiState.proteinRatio(),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        MacroNutrientCard(
                            amount = "${uiState.currentCarbs}g",
                            maxAmount = "${uiState.maxCarbs}g",
                            title = s.common.carbs,
                            iconResId = R.drawable.wheat,
                            donutColor = colorResource(id = R.color.carbsColor),
                            percentage = uiState.carbsRatio(),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        MacroNutrientCard(
                            amount = "${uiState.currentFats}g",
                            maxAmount = "${uiState.maxFats}g",
                            title = s.common.fat,
                            iconResId = R.drawable.avocado,
                            donutColor = colorResource(id = R.color.fatColor),
                            percentage = uiState.fatsRatio(),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = s.home.addedToday,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppTheme.colors.onBackground
                    )
                    IconButton(onClick = { navController.navigate(CustomFoodRoute) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = s.common.add,
                            tint = AppTheme.colors.onBackground
                        )
                    }
                }
                if (uiState.recentlyAddedItems.isEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    EmptyRecentlyAddedList()
                }
            }
        }
        items(uiState.recentlyAddedItems, key = {
            it.id
        }) { item ->
            FoodItemCard(
                foodItem = item,
                progress = uiState.loadingProgressForItems[item.id] ?: 0,
                onClick = {
                    navController.navigate(FoodDetailRoute(item.id))
                }
            )
        }

    }
}

@Composable
private fun EmptyRecentlyAddedList() {
    val s = LocalStrings.current
    KalaiCard(
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
            Text(
                text = s.home.emptyTitle,
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.onBackground
            )
            Text(
                text = s.home.emptySubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.onBackgroundSecondary
            )
        }
    }
}
