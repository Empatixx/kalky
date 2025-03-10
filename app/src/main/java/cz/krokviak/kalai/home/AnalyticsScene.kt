package cz.krokviak.kalai.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.krokviak.kalai.home.components.CalorieBarChart
import cz.krokviak.kalai.home.components.MacroNutrientRatioDonutChart

@Composable
fun AnalyticsScene(
    mainViewModel: MainViewModel
) {
    val uiState by mainViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        PeriodButtonsSegmented(
            selectedRange = uiState.analyticsRange,
            onRangeSelected = { newRange ->
                mainViewModel.setAnalyticsRange(newRange)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Donut Chart card
        MacroNutrientRatioDonutChart(
            stats = uiState.dailyStats,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        CalorieBarChart(
            stats = uiState.dailyStats,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}

@Composable
fun PeriodButtonsSegmented(
    selectedRange: AnalyticsRange,
    onRangeSelected: (AnalyticsRange) -> Unit
) {
    val rangeOptions = listOf(
        AnalyticsRange.WEEK to "1 týden",
        AnalyticsRange.TWO_WEEKS to "2 týdny",
        AnalyticsRange.THREE_WEEKS to "3 týdny",
        AnalyticsRange.MONTH to "1 měsíc"
    )

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        rangeOptions.forEachIndexed { index, (range, label) ->
            val isSelected = (range == selectedRange)

            // Compute the shape based on position:
            val buttonShape = when (index) {
                0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                rangeOptions.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                else -> RectangleShape
            }

            OutlinedButton(
                onClick = { onRangeSelected(range) },
                shape = buttonShape,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) {
                        // A subtle highlight or fill if selected
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    } else {
                        Color.Transparent
                    },
                    contentColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Gray
                    }
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
