package cz.krokviak.kalai.home.components

import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import cz.krokviak.kalai.R
import cz.krokviak.kalai.home.DailyMarkerView
import cz.krokviak.kalai.home.DailyStats

@Composable
fun CalorieBarChart(
    stats: List<DailyStats>,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
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
                stats = stats,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
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
