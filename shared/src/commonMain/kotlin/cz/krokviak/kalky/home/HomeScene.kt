package cz.krokviak.kalky.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.NoteAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cz.krokviak.kalky.theme.MacroColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.home.components.CalorieCard
import cz.krokviak.kalky.home.components.FoodItemCard
import cz.krokviak.kalky.home.components.MacroNutrientCard
import cz.krokviak.kalky.home.components.WeekDatePicker
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.i18n.LocalStrings
import cz.krokviak.kalky.ui.LocalDimensions
import cz.krokviak.kalky.ui.components.KalkyCard
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScene(
    uiState: MainUiState,
    onFoodClick: (Long) -> Unit,
    onFoodLongClick: (Long) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onTodayClick: () -> Unit,
    onAddCustomClick: () -> Unit,
    onEditTargetsClick: () -> Unit,
    onSelectionClear: () -> Unit,
    onSaveSelectionAsCustom: () -> Unit,
    onDeleteSelection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dims = LocalDimensions.current
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dims.itemSpacing),
            contentPadding = PaddingValues(dims.screenPadding)
        ) {
            item {
                val s = LocalStrings.current
                Column(modifier = Modifier.fillMaxWidth()) {
                    WeekDatePicker(
                        currentDate = uiState.currentDate,
                        isToday = uiState.isToday,
                        onDateChange = onDateChange,
                        onTodayClick = onTodayClick
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onEditTargetsClick
                            )
                    ) {
                        CalorieCard(
                            uiState.currentCalories,
                            uiState.maxCalories,
                            uiState.calorieRatio(),
                            currentStreak = uiState.currentStreak,
                        )

                        Spacer(modifier = Modifier.height(dims.itemSpacing))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MacroNutrientCard(
                                amount = "${uiState.currentProtein}g",
                                maxAmount = "${uiState.maxProtein}g",
                                title = s.common.protein,
                                icon = Icons.Default.Restaurant,
                                donutColor = MacroColors.protein,
                                percentage = uiState.proteinRatio(),
                            )
                            Spacer(modifier = Modifier.width(dims.halfSpacing))
                            MacroNutrientCard(
                                amount = "${uiState.currentCarbs}g",
                                maxAmount = "${uiState.maxCarbs}g",
                                title = s.common.carbs,
                                icon = Icons.Default.Spa,
                                donutColor = MacroColors.carbs,
                                percentage = uiState.carbsRatio(),
                            )
                            Spacer(modifier = Modifier.width(dims.halfSpacing))
                            MacroNutrientCard(
                                amount = "${uiState.currentFats}g",
                                maxAmount = "${uiState.maxFats}g",
                                title = s.common.fat,
                                icon = Icons.Default.Eco,
                                donutColor = MacroColors.fat,
                                percentage = uiState.fatsRatio(),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(dims.halfSpacing))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = s.home.addedToday,
                            fontSize = dims.fontTitle,
                            fontWeight = FontWeight.ExtraBold,
                            color = AppTheme.colors.onBackground
                        )
                        IconButton(onClick = onAddCustomClick) {
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
            items(uiState.recentlyAddedItems, key = { it.id }) { item ->
                FoodItemCard(
                    foodItem = item,
                    isSelected = item.id in uiState.selectedFoodIds,
                    onClick = { onFoodClick(item.id) },
                    onLongClick = { onFoodLongClick(item.id) }
                )
            }
        }

        if (uiState.isSelectionMode) {
            val s = LocalStrings.current
            KalkyCard(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(dims.screenPadding),
                shape = RoundedCornerShape(16.dp),
                color = AppTheme.colors.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onSelectionClear) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = s.common.close,
                            tint = AppTheme.colors.onBackground
                        )
                    }
                    Text(
                        text = "${s.home.selected}: ${uiState.selectedFoodIds.size}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colors.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onSaveSelectionAsCustom) {
                        Icon(
                            imageVector = Icons.Outlined.NoteAdd,
                            contentDescription = s.home.saveAsCustom,
                            tint = AppTheme.colors.onBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = onDeleteSelection) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = s.home.deleteSelected,
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyRecentlyAddedList() {
    val s = LocalStrings.current
    KalkyCard(
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
