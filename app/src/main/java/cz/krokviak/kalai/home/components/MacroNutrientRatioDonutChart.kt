package cz.krokviak.kalai.home.components

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import cz.krokviak.kalai.R
import cz.krokviak.kalai.home.DailyStats

@Composable
fun MacroNutrientRatioDonutChart(
    stats: List<DailyStats>,
    modifier: Modifier = Modifier
){
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth(),
                border = CardDefaults.outlinedCardBorder()
    ) {
        // Donut Chart Composable
        MPACDonutChart(
            stats = stats,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}

@Composable
fun MPACDonutChart(
    stats: List<DailyStats>,
    modifier: Modifier = Modifier
) {
    // 1. Calculate total calories for each macro across all days
    val totalProteinCals = stats.sumOf { it.protein * 4 }
    val totalFatCals = stats.sumOf { it.fat * 9 }
    val totalCarbsCals = stats.sumOf { it.carbs * 4 }
    val totalCals = totalProteinCals + totalFatCals + totalCarbsCals

    // 2. Compute percentages (handle the zero case)
    val proteinPercent = if (totalCals > 0) (totalProteinCals * 100f / totalCals) else 0f
    val carbsPercent = if (totalCals > 0) (totalCarbsCals * 100f / totalCals) else 0f
    val fatPercent = if (totalCals > 0) (totalFatCals * 100f / totalCals) else 0f

    // 3. Lay out: A Row with three Columns, then the Donut Chart
    Column(modifier = modifier) {

        // -- Row with 3 columns (Bílkoviny, Sacharidy, Tuky) --
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
// 1) Bílkoviny
            Column(
                modifier = Modifier.padding(end = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(colorResource(id = R.color.proteinColor))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = String.format("%.0f%%", proteinPercent),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
                Text(
                    text = "Bílkoviny",
                    fontSize = 16.sp
                )
            }

            // 2) Sacharidy
            Column(
                modifier = Modifier.padding(end = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(colorResource(id = R.color.carbsColor))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = String.format("%.0f%%", carbsPercent),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
                Text(
                    text = "Sacharidy",
                    fontSize = 16.sp
                )
            }
            // 3) Tuky
            Column(
                modifier = Modifier.padding(end = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(colorResource(id = R.color.fatColor))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = String.format("%.0f%%", fatPercent),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
                Text(
                    text = "Tuky",
                    fontSize = 16.sp
                )
            }
        }
        // -- Donut Chart itself (AndroidView) --
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),  // Allow the chart to expand
            factory = { context ->
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
                    setUsePercentValues(false)   // We are handling percentages manually

                    // Disable legend (optional)
                    legend.isEnabled = false

                    // Disable user interaction (optional)
                    setTouchEnabled(false)
                    isHighlightPerTapEnabled = false

                    // Hide slice entry labels
                    setDrawEntryLabels(false)
                }

                // 4. Build the PieEntries (only if macro > 0)
                val entries = mutableListOf<PieEntry>()
                if (totalProteinCals > 0) {
                    entries.add(PieEntry(totalProteinCals.toFloat(), "Bílkoviny"))
                }
                if (totalCarbsCals > 0) {
                    entries.add(PieEntry(totalCarbsCals.toFloat(), "Sacharidy"))
                }
                if (totalFatCals > 0) {
                    entries.add(PieEntry(totalFatCals.toFloat(), "Tuky"))
                }

                // 5. Create a PieDataSet
                val dataSet = PieDataSet(entries, "Makra").apply {
                    colors = listOf(
                        ContextCompat.getColor(context, R.color.proteinColor), // Bílkoviny
                        ContextCompat.getColor(context, R.color.carbsColor),   // Sacharidy
                        ContextCompat.getColor(context, R.color.fatColor)      // Tuky
                    )
                    sliceSpace = 2f
                    // Hide value labels on slices
                    setDrawValues(false)
                }

                // 6. Create PieData
                val pieData = PieData(dataSet)

                pieChart.data = pieData
                pieChart.invalidate() // Refresh
                pieChart
            }
        )
    }
}