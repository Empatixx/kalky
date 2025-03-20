// AnalyticsPage.kt
package cz.krokviak.kalai.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.krokviak.kalai.analytics.components.NutrientCalorieCard
import cz.krokviak.kalai.analytics.components.WeightLineChart
import io.github.alexzhirkevich.cupertino.CupertinoSegmentedControl
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AnalyticsPage(
    analyticsViewModel: AnalyticsViewModel,
    uiState: AnalyticsUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1) Display the last 7 days' weights in a line chart
        WeightLineChart(weights = uiState.weights)

        // 2) Display the last 7 days' macros in bars
        NutrientCalorieCard(bars = uiState.caloriesBars)
    }
}
