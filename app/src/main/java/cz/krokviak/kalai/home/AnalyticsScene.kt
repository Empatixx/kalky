package cz.krokviak.kalai.home

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import cz.krokviak.kalai.R

@Composable
fun AnalyticsScene(
    mainViewModel: MainViewModel
) {
    val uiState by mainViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1) Range selector row (non-rounded toggles)
        RangeSelectorRow(
            selectedRange = uiState.analyticsRange,
            onRangeSelected = mainViewModel::onAnalyticsRangeChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2) Outlined card that holds daily average + chart (400dp tall)
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            // Inside the card, a Column for the daily average + the chart
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Show only Denní průměr kalorií
                if (uiState.dailyStats.isNotEmpty()) {
                    val totalCals = uiState.dailyStats.sumOf { it.totalCalories }
                    val avgCals = totalCals / uiState.dailyStats.size

                    Text(
                        text = "Denní průměr kalorií: $avgCals",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 3) MPAndroidChart-based stacked bar chart
                MPACStackedBarChart(
                    stats = uiState.dailyStats,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // allow chart to expand
                )
            }
        }
    }
}

@Composable
fun RangeSelectorRow(
    selectedRange: AnalyticsRange,
    onRangeSelected: (AnalyticsRange) -> Unit
) {
    val items = listOf(
        AnalyticsRange.WEEK to "WEEK",
        AnalyticsRange.TWO_WEEKS to "2 WEEKS",
        AnalyticsRange.THREE_WEEKS to "3 WEEKS",
        AnalyticsRange.MONTH to "1 MONTH"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { (range, label) ->
            val isSelected = (selectedRange == range)
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(0.dp))  // No corner rounding
                    .background(if (isSelected) Color.Black else Color.LightGray)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onRangeSelected(range) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun MPACStackedBarChart(
    stats: List<DailyStats>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            com.github.mikephil.charting.charts.BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Remove chart description
                description.isEnabled = false

                // Disable pinch zoom and all touches
                setPinchZoom(false)
                setTouchEnabled(false)

                // Remove grid background
                setDrawGridBackground(false)

                // Remove legend
                legend.isEnabled = false

                // X Axis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.setDrawAxisLine(false)  // remove bottom axis line
                xAxis.textSize = 14f // bigger text

                // Y Axis
                axisRight.isEnabled = false
                axisLeft.setDrawGridLines(false)
                axisLeft.setDrawAxisLine(false)  // remove left axis line
                axisLeft.axisMinimum = 0f
                axisLeft.axisMaximum = 2000f     // you can adjust if needed
                axisLeft.granularity = 500f
                axisLeft.labelCount = 4  // We want 4 “ticks” ideally: 0, 500, 1000, 2000
                axisLeft.textSize = 14f  // bigger text

                // We'll provide a custom ValueFormatter for axisLeft
                axisLeft.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return when (value) {
                            0f -> "0"
                            500f -> "500"
                            1000f -> "1000"
                            2000f -> "2000"
                            else -> ""
                        }
                    }
                }
            }
        },
        update = { chart ->
            // Build stacked bars in order: Carbs, Fat, Protein
            val entries = stats.mapIndexed { index, dailyStats ->
                val carbsCals = dailyStats.carbs * 4f
                val fatCals   = dailyStats.fat * 9f
                val proteinCals = dailyStats.protein * 4f
                com.github.mikephil.charting.data.BarEntry(
                    index.toFloat(),
                    floatArrayOf(carbsCals, fatCals, proteinCals)
                )
            }

            // Single DataSet for the stacked bars
            val dataSet = com.github.mikephil.charting.data.BarDataSet(entries, "").apply {
                setDrawValues(false) // hide value labels on top of bars
                setColors(
                    intArrayOf(
                        R.color.carbsColor,  // Carbs
                        R.color.fatColor,    // Fat
                        R.color.proteinColor // Protein
                    ),
                    chart.context
                )
                // We removed legend, so no stack labels needed:
                // stackLabels = arrayOf("Carbs", "Fat", "Protein")
            }

            val barData = com.github.mikephil.charting.data.BarData(dataSet).apply {
                barWidth = 0.6f
            }

            chart.data = barData

            // Provide custom labels for X axis (day labels)
            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val idx = value.toInt().coerceIn(stats.indices)
                    return stats[idx].dayLabel
                }
            }

            chart.invalidate() // Refresh the chart
        },
        modifier = modifier
    )
}
