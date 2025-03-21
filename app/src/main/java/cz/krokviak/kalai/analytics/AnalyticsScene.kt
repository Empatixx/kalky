// AnalyticsPage.kt
package cz.krokviak.kalai.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.krokviak.kalai.analytics.components.NutrientCalorieCard
import cz.krokviak.kalai.analytics.components.WeightLineChart
import io.github.alexzhirkevich.cupertino.CupertinoSegmentedControl
import io.github.alexzhirkevich.cupertino.CupertinoSegmentedControlIndicator
import io.github.alexzhirkevich.cupertino.CupertinoSegmentedControlTab
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AnalyticsPage(
    analyticsViewModel: AnalyticsViewModel,
    uiState: AnalyticsUiState,
    modifier: Modifier = Modifier
) {
    // Create mapping of labels (in Czech) to each enum value
    val rangeLabels = listOf("1 Týden", "2 Týdny", "3 Týdny", "1 Měsíc")

    // Compute which index is currently selected based on uiState
    val selectedIndex = when (uiState.selectedRange) {
        AnalyticsRange.WEEK       -> 0
        AnalyticsRange.TWO_WEEKS  -> 1
        AnalyticsRange.THREE_WEEKS -> 2
        AnalyticsRange.MONTH      -> 3
    }

    Column(
        modifier = modifier
            .padding(24.dp) // Same padding as your charts/card
            .fillMaxWidth(), // Ensures the segmented control stretches to full width
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ---- CupertinoSegmentedControl ----
        CupertinoSegmentedControl(
            selectedTabIndex = selectedIndex,
            modifier = Modifier.fillMaxWidth(), // match width of charts
            indicator = { tabPositions ->
                // Use the default sliding indicator or customize
                CupertinoSegmentedControlIndicator(
                    selectedTabIndex = selectedIndex,
                    tabPositions = tabPositions
                )
            },
            // You can also override colors/shape if needed:
            // colors = CupertinoSegmentedControlDefaults.colors(),
            // shape = CupertinoSegmentedControlDefaults.shape,
            tabs = {
                rangeLabels.forEachIndexed { index, label ->
                    CupertinoSegmentedControlTab(
                        onClick = {
                            // Update ViewModel or state with new selection
                            analyticsViewModel.updateRange(
                                AnalyticsRange.values()[index]
                            )
                        },
                        isSelected = index == selectedIndex
                    ) {
                        Text(text = label)
                    }
                }
            }
        )

        // ---- Your existing content below ----
        // 1) Display the last 7 days' weights in a line chart
        WeightLineChart(weights = uiState.weights)

        // 2) Display the last 7 days' macros in bars
        NutrientCalorieCard(bars = uiState.caloriesBars)
    }
}

