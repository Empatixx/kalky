package cz.krokviak.kalky.analytics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.krokviak.kalky.analytics.components.NutrientCalorieCard
import cz.krokviak.kalky.analytics.components.WeightLineChart
import cz.krokviak.kalky.analytics.components.WheelDatePickerInline
import cz.krokviak.kalky.i18n.LocalStrings
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.theme.KalkyAccents
import cz.krokviak.kalky.ui.LocalDimensions
import cz.krokviak.kalky.ui.components.KalkyCard
import kotlinx.datetime.LocalDate

private enum class DateField { START, END }

@Composable
fun AnalyticsPage(
    analyticsViewModel: AnalyticsViewModel,
    uiState: AnalyticsUiState,
    modifier: Modifier = Modifier
) {
    val dims = LocalDimensions.current
    val strings = LocalStrings.current
    var activeDateField by remember { mutableStateOf<DateField?>(null) }
    val startDateLabel = remember(uiState.startDate) { formatDate(uiState.startDate) }
    val endDateLabel = remember(uiState.endDate) { formatDate(uiState.endDate) }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(dims.screenPadding),
        verticalArrangement = Arrangement.spacedBy(dims.itemSpacing)
    ) {
        item("dateRange") {
            KalkyCard(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = AppTheme.colors.border,
                        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
                    )
                    .fillMaxWidth(),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
                contentPadding = PaddingValues(0.dp),
            ) {
                Column {
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
                            text = strings.analytics.dateStart,
                            color = AppTheme.colors.onBackground,
                            fontSize = dims.fontBody,
                        )
                        Text(
                            text = startDateLabel,
                            color = if (activeDateField == DateField.START) KalkyAccents.iosRed else AppTheme.colors.onBackground,
                            fontSize = dims.fontBody,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

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
                            text = strings.analytics.dateEnd,
                            color = AppTheme.colors.onBackground,
                            fontSize = dims.fontBody,
                        )
                        Text(
                            text = endDateLabel,
                            color = if (activeDateField == DateField.END) KalkyAccents.iosRed else AppTheme.colors.onBackground,
                            fontSize = dims.fontBody,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

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
        }
        item("weightChart") {
            WeightLineChart(weights = uiState.weights)
        }
        item("nutrientChart") {
            NutrientCalorieCard(bars = uiState.caloriesBars)
        }
    }
}

private fun formatDate(date: LocalDate): String {
    return "${date.dayOfMonth}.${date.monthNumber}.${date.year}"
}
