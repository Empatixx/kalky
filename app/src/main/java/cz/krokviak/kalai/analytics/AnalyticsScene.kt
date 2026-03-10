package cz.krokviak.kalai.analytics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.analytics.components.NutrientCalorieCard
import cz.krokviak.kalai.analytics.components.WeightLineChart
import cz.krokviak.kalai.theme.AppTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

private enum class DateField { START, END }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsPage(
    analyticsViewModel: AnalyticsViewModel,
    uiState: AnalyticsUiState,
    modifier: Modifier = Modifier
) {
    var activeDateField by remember { mutableStateOf<DateField?>(null) }

    Column(
        modifier = modifier
            .padding(24.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DateChip(
                label = "Od",
                date = uiState.startDate,
                onClick = { activeDateField = DateField.START },
                modifier = Modifier.weight(1f)
            )
            DateChip(
                label = "Do",
                date = uiState.endDate,
                onClick = { activeDateField = DateField.END },
                modifier = Modifier.weight(1f)
            )
        }

        WeightLineChart(weights = uiState.weights)
        NutrientCalorieCard(bars = uiState.caloriesBars)
    }

    if (activeDateField != null) {
        val currentDate = when (activeDateField) {
            DateField.START -> uiState.startDate
            DateField.END -> uiState.endDate
            null -> return
        }
        val initialMillis = currentDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { activeDateField = null },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val selected = Instant.fromEpochMilliseconds(millis)
                            .toLocalDateTime(TimeZone.UTC).date
                        when (activeDateField) {
                            DateField.START -> analyticsViewModel.setStartDate(selected)
                            DateField.END -> analyticsViewModel.setEndDate(selected)
                            null -> {}
                        }
                    }
                    activeDateField = null
                }) {
                    Text("Potvrdit")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDateField = null }) {
                    Text("Zrušit")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun DateChip(
    label: String,
    date: LocalDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, AppTheme.colors.border),
        color = AppTheme.colors.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = AppTheme.colors.onBackgroundSecondary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formatDate(date),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = AppTheme.colors.onBackground
            )
        }
    }
}

private fun formatDate(date: LocalDate): String {
    return "${date.dayOfMonth}.${date.monthNumber}.${date.year}"
}
