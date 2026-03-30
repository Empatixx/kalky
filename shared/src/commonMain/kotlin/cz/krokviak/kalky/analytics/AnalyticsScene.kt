package cz.krokviak.kalky.analytics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.krokviak.kalky.analytics.components.NutrientCalorieCard
import cz.krokviak.kalky.analytics.components.WeightLineChart
import cz.krokviak.kalky.analytics.components.WheelDatePickerInline
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.ui.LocalDimensions
import cz.krokviak.kalky.ui.components.KalkyCard
import kotlinx.datetime.LocalDate

private enum class DateField { START, END }

private val IOS_RED = Color(0xFFFF3B30)

@Composable
fun AnalyticsPage(
    analyticsViewModel: AnalyticsViewModel,
    uiState: AnalyticsUiState,
    modifier: Modifier = Modifier
) {
    val dims = LocalDimensions.current
    var activeDateField by remember { mutableStateOf<DateField?>(null) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(dims.screenPadding)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dims.itemSpacing)
    ) {
        // iOS-style date range card
        KalkyCard(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = AppTheme.colors.border,
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(0.dp),
        ) {
            Column {
                // Start date row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            activeDateField = if (activeDateField == DateField.START) null else DateField.START
                        }
                        .padding(horizontal = dims.cardPadding, vertical = dims.halfSpacing * 1.5f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Začátek",
                        color = AppTheme.colors.onBackground,
                        fontSize = dims.fontBody,
                    )
                    Text(
                        text = formatDate(uiState.startDate),
                        color = if (activeDateField == DateField.START) IOS_RED else AppTheme.colors.onBackground,
                        fontSize = dims.fontBody,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                // Inline picker for start date
                AnimatedVisibility(
                    visible = activeDateField == DateField.START,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        HorizontalDivider(color = AppTheme.colors.border)
                        WheelDatePickerInline(
                            initialDate = uiState.startDate,
                            onDateChanged = { analyticsViewModel.setStartDate(it) },
                            modifier = Modifier.padding(horizontal = dims.halfSpacing, vertical = dims.halfSpacing)
                        )
                    }
                }

                HorizontalDivider(color = AppTheme.colors.border)

                // End date row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            activeDateField = if (activeDateField == DateField.END) null else DateField.END
                        }
                        .padding(horizontal = dims.cardPadding, vertical = dims.halfSpacing * 1.5f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Konec",
                        color = AppTheme.colors.onBackground,
                        fontSize = dims.fontBody,
                    )
                    Text(
                        text = formatDate(uiState.endDate),
                        color = if (activeDateField == DateField.END) IOS_RED else AppTheme.colors.onBackground,
                        fontSize = dims.fontBody,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                // Inline picker for end date
                AnimatedVisibility(
                    visible = activeDateField == DateField.END,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        HorizontalDivider(color = AppTheme.colors.border)
                        WheelDatePickerInline(
                            initialDate = uiState.endDate,
                            onDateChanged = { analyticsViewModel.setEndDate(it) },
                            modifier = Modifier.padding(horizontal = dims.halfSpacing, vertical = dims.halfSpacing)
                        )
                    }
                }
            }
        }

        WeightLineChart(weights = uiState.weights)
        NutrientCalorieCard(bars = uiState.caloriesBars)
    }
}

private fun formatDate(date: LocalDate): String {
    return "${date.dayOfMonth}.${date.monthNumber}.${date.year}"
}
