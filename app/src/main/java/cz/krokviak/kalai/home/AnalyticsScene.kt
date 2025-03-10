package cz.krokviak.kalai.home

import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import cz.krokviak.kalai.R
import java.security.KeyStore

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
        // Donut Chart card
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            // Donut Chart Composable
            MPACDonutChart(
                stats = uiState.dailyStats,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stacked Bar Chart card
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Column
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "1000",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Celkem kalorií",
                            fontSize = 16.sp
                        )
                    }
                    // Right Column
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "2000",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Průměr kalorií",
                            fontSize = 16.sp
                        )
                    }
                }

                // Stacked Bar Chart
                MPACStackedBarChart(
                    stats = uiState.dailyStats,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
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
        modifier = modifier,
        factory = { context ->

            // 1. Create and configure the BarChart
            val chart = BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Chart appearance and interaction
                legend.isEnabled = false
                description.isEnabled = false
                setDrawGridBackground(false)
                axisRight.isEnabled = false

                setTouchEnabled(true)
                isHighlightPerTapEnabled = true
                setPinchZoom(false)
                setDoubleTapToZoomEnabled(false)
                setDragEnabled(false)
                setScaleEnabled(false)

                // Marker (tooltip) for total calories
                marker = DailyMarkerView(context, stats)
                setDrawMarkers(true)

                // Highlight entire bar instead of single stacked segment
                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        if (h != null) {
                            val entireBarHighlight = Highlight(h.x, h.dataSetIndex, -1)
                            highlightValue(entireBarHighlight, false)
                        }
                    }
                    override fun onNothingSelected() {}
                })

                // 2. Configure the X-axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    granularity = 1f
                    labelCount = stats.size
                    textSize = 14f  // Bigger X-axis label text size

                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return stats.getOrNull(index)?.dayLabel ?: ""
                        }
                    }
                }

                // 3. Configure the Left Axis
                axisLeft.apply {
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    axisMinimum = 0f
                    textSize = 14f  // Bigger Left-axis label text size

                    // Automatically determine maximum Y
                    val maxCalories = stats.maxOfOrNull { it.totalCalories } ?: 0
                    val roundedMax = ((maxCalories + 499) / 500) * 500
                    axisMaximum = roundedMax.toFloat().coerceAtLeast(2000f)

                    granularity = 500f
                    setLabelCount((axisMaximum / 500f).toInt() + 1, true)
                }
            }

            // 4. Build the data entries for stacked bars
            val barEntries = stats.mapIndexed { index, dayStat ->
                val proteinValue = dayStat.protein * 4f  // Bílkoviny
                val fatValue = dayStat.fat * 9f          // Tuky
                val carbsValue = dayStat.carbs * 4f      // Sacharidy

                BarEntry(
                    index.toFloat(),
                    floatArrayOf(proteinValue, carbsValue, fatValue)
                )
            }

            // 5. Create the BarDataSet
            val dataSet = BarDataSet(barEntries, "Macros").apply {
                setDrawValues(false)
                colors = listOf(
                    ContextCompat.getColor(context, R.color.proteinColor),
                    ContextCompat.getColor(context, R.color.carbsColor),
                    ContextCompat.getColor(context, R.color.fatColor)
                )
                stackLabels = arrayOf("Bílkoviny", "Sacharidy", "Tuky")
            }

            // 6. Prepare the BarData
            val barData = BarData(dataSet).apply {
                barWidth = 0.5f
            }

            // 7. Finalize and refresh the chart
            chart.data = barData
            chart.invalidate()
            chart
        }
    )
}



@Composable
fun MPACDonutChart(
    stats: List<DailyStats>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->

            // 1. Create and configure the PieChart
            val pieChart = PieChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Disable description label
                description.isEnabled = false

                // Donut style
                isDrawHoleEnabled = true
                holeRadius = 50f
                setUsePercentValues(true)

                // Enable/disable labels on slices
                setDrawEntryLabels(true)

                // Disable legend (optional)
                legend.isEnabled = false

                // ---- Disable interactions ----
                setTouchEnabled(false)
                isHighlightPerTapEnabled = false
                setClickable(false)

                // Ensure entry labels (the slice "titles") are black
                setEntryLabelColor(android.graphics.Color.BLACK)
                setEntryLabelTextSize(14f)
            }

            // 2. Calculate total calories for each macro across all days
            val totalProteinCals = stats.sumOf { it.protein * 4 }
            val totalFatCals = stats.sumOf { it.fat * 9 }
            val totalCarbsCals = stats.sumOf { it.carbs * 4 }

            // 3. Create PieEntries for each macro (only if > 0)
            val entries = ArrayList<PieEntry>()
            if (totalProteinCals > 0) {
                entries.add(PieEntry(totalProteinCals.toFloat(), "Bílkoviny"))
            }
            if (totalCarbsCals > 0) {
                entries.add(PieEntry(totalCarbsCals.toFloat(), "Sacharidy"))
            }
            if (totalFatCals > 0) {
                entries.add(PieEntry(totalFatCals.toFloat(), "Tuky"))
            }

            // 4. Create a PieDataSet with your three colors
            val dataSet = PieDataSet(entries, "Makra").apply {
                // Reuse colors from your barchart
                colors = listOf(
                    ContextCompat.getColor(context, R.color.proteinColor), // Bílkoviny
                    ContextCompat.getColor(context, R.color.carbsColor),   // Sacharidy
                    ContextCompat.getColor(context, R.color.fatColor)      // Tuky
                )
                sliceSpace = 2f

                // Show values on slices
                setDrawValues(true)
                // Format them as percentages
                setValueFormatter(PercentFormatter(pieChart))
                // Set text size/color for slice values
                valueTextSize = 14f
                valueTextColor = android.graphics.Color.BLACK
            }

            // 5. Create PieData and set it to pieChart
            val pieData = PieData(dataSet).apply {
                setValueTextSize(24f)
                setValueTextColor(android.graphics.Color.BLACK)
            }

            pieChart.data = pieData
            pieChart.invalidate() // Refresh the chart
            pieChart
        }
    )
}

