package cz.krokviak.kalai.analytics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.krokviak.kalai.home.MainUiState
import cz.krokviak.kalai.home.MainViewModel
import cz.krokviak.kalai.analytics.components.CalorieBarChart
import cz.krokviak.kalai.analytics.components.MacroNutrientRatioDonutChart
import io.github.alexzhirkevich.cupertino.CupertinoSegmentedControl
import io.github.alexzhirkevich.cupertino.CupertinoSegmentedControlTab
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AnalyticsPage(
    mainViewModel: MainViewModel,
    uiState: MainUiState,
    modifier: Modifier = Modifier
) {
    val rangeLabels = mapOf(
        AnalyticsRange.WEEK to "1 Týden",
        AnalyticsRange.TWO_WEEKS to "2 Týdny",
        AnalyticsRange.THREE_WEEKS to "3 Týdny",
        AnalyticsRange.MONTH to "1 Měsíc"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        CupertinoSegmentedControl(
            paddingValues = PaddingValues(0.dp),
            selectedTabIndex = AnalyticsRange.values().indexOf(uiState.analyticsRange),
        ) {
            AnalyticsRange.values().forEachIndexed { index, range ->
                CupertinoSegmentedControlTab(
                    onClick = {
                        // Call your VM method to change analytics range
                        mainViewModel.setAnalyticsRange(range)
                    },
                    isSelected = (uiState.analyticsRange == range)
                ) {
                    // Display each range's label
                    CupertinoText(text = rangeLabels[range] ?: "")
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // 2) Donut Chart
        MacroNutrientRatioDonutChart(
            stats = uiState.dailyStats,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3) Bar Chart
        CalorieBarChart(
            stats = uiState.dailyStats,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}