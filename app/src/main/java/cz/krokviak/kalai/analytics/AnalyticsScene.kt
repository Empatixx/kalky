package cz.krokviak.kalai.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.krokviak.kalai.analytics.components.NutrientCalorieCard
import cz.krokviak.kalai.analytics.components.WeightLineChart
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
        WeightLineChart(
            weights = uiState.weights,
        )
        NutrientCalorieCard(
            bars = uiState.caloriesBars
        )
    }
}