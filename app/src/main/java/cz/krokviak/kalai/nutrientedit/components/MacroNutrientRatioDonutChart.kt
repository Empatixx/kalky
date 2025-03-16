package cz.krokviak.kalai.nutrientedit.components

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun MacroNutrientRatioDonutChart(
    protein: Int = 0,
    carbs: Int = 0,
    fat: Int = 0,
    modifier: Modifier = Modifier
) {
    CupertinoSection(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp)),
        contentPadding = PaddingValues(0.dp),
        dividerPadding = PaddingValues(0.dp),
    ) {
        MacroNutrientDonutSection(protein = protein, carbs = carbs, fat = fat)
    }
}

@Composable
fun MacroNutrientDonutSection(
    protein: Int,
    carbs: Int,
    fat: Int,
    modifier: Modifier = Modifier
) {
    // 1. Calculate total calories for each macro
    val totalProteinCals = protein * 4
    val totalFatCals = fat * 9
    val totalCarbsCals = carbs * 4
    val totalCals = totalProteinCals + totalFatCals + totalCarbsCals

    // 2. Compute percentages (handle zero totals gracefully)
    val proteinPercent = if (totalCals > 0) (totalProteinCals * 100f / totalCals) else 0f
    val carbsPercent = if (totalCals > 0) (totalCarbsCals * 100f / totalCals) else 0f
    val fatPercent = if (totalCals > 0) (totalFatCals * 100f / totalCals) else 0f

    Column(modifier = modifier.padding(16.dp)) {
        // Legend row
        MacroNutrientLegendRow(
            proteinPercent = proteinPercent,
            carbsPercent = carbsPercent,
            fatPercent = fatPercent
        )

        // Donut chart
        MacroNutrientDonutChart(
            totalProteinCals = totalProteinCals,
            totalCarbsCals = totalCarbsCals,
            totalFatCals = totalFatCals,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(1f) // Let the chart expand to fill remaining space
        )
    }
}

@Composable
private fun MacroNutrientLegendRow(
    proteinPercent: Float,
    carbsPercent: Float,
    fatPercent: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(
            label = "Bílkoviny",
            percent = proteinPercent,
            colorId = R.color.proteinColor
        )
        LegendItem(
            label = "Sacharidy",
            percent = carbsPercent,
            colorId = R.color.carbsColor
        )
        LegendItem(
            label = "Tuky",
            percent = fatPercent,
            colorId = R.color.fatColor
        )
    }
}

@Composable
private fun LegendItem(
    label: String,
    percent: Float,
    colorId: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(end = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(colorResource(id = colorId))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = String.format("%.0f%%", percent),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
        Text(
            text = label,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun MacroNutrientDonutChart(
    totalProteinCals: Int,
    totalCarbsCals: Int,
    totalFatCals: Int,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PieChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Donut style
                isDrawHoleEnabled = true
                holeRadius = 50f

                // Disable unneeded labels
                description.isEnabled = false
                legend.isEnabled = false
                setDrawEntryLabels(false)

                // Disable user interaction
                setTouchEnabled(false)
                isHighlightPerTapEnabled = false
            }.also { pieChart ->
                // Build the PieEntries (only if macro > 0)
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

                // Create a PieDataSet
                val dataSet = PieDataSet(entries, "Makra").apply {
                    colors = listOf(
                        ContextCompat.getColor(context, R.color.proteinColor),
                        ContextCompat.getColor(context, R.color.carbsColor),
                        ContextCompat.getColor(context, R.color.fatColor)
                    )
                    sliceSpace = 2f
                    // Hide value labels on slices
                    setDrawValues(false)
                }

                pieChart.data = PieData(dataSet)
                pieChart.invalidate() // Refresh the chart
            }
        }
    )
}
