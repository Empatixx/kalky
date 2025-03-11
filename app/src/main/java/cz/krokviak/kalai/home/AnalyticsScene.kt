package cz.krokviak.kalai.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.krokviak.kalai.home.components.CalorieBarChart
import cz.krokviak.kalai.home.components.MacroNutrientRatioDonutChart
import io.github.alexzhirkevich.cupertino.CupertinoSegmentedControl
import io.github.alexzhirkevich.cupertino.CupertinoSegmentedControlTab
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AnalyticsScene(
    mainViewModel: MainViewModel
) {
    val uiState by mainViewModel.uiState.collectAsState()

    val rangeLabels = mapOf(
        AnalyticsRange.WEEK to "1 Týden",
        AnalyticsRange.TWO_WEEKS to "2 Týdny",
        AnalyticsRange.THREE_WEEKS to "3 Týdny",
        AnalyticsRange.MONTH to "1 Měsíc"
    )

    Column(
        modifier = Modifier
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

