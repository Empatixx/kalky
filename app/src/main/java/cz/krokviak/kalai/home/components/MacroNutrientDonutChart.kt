package cz.krokviak.kalai.home.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import cz.krokviak.kalai.R
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.CupertinoText

@Composable
fun MacroNutrientDonutChart(
    modifier: Modifier = Modifier,
    percentage: Float,
    activeColor: Color,
    inactiveColor: Color = colorResource(id = R.color.lightBlueGray),
    holeRadius: Float = 80f,
    centerIcon: ImageVector? = null,
    centerIconSize: Dp = 32.dp,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // AndroidView for MPAndroidChart
        AndroidView(
            factory = { context ->
                createPieChart(
                    context = context,
                    percentage = percentage,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    holeRadius = holeRadius
                )
            },
            update = { chart ->
                // Update the chart whenever percentage changes
                val entries = buildPieEntries(percentage)
                val dataSet = PieDataSet(entries, "").apply {
                    colors = listOf(
                        activeColor.toArgb(),
                        inactiveColor.toArgb()
                    )
                    setDrawValues(false)
                }
                chart.data = PieData(dataSet)
                chart.invalidate()  // force redraw
            },
            modifier = Modifier.matchParentSize()
        )

        // Overlay icon if provided
        if (centerIcon != null) {
            Box(
                modifier = Modifier
                    .size(centerIconSize * 2)
                    .clip(CircleShape)
                    .background(colorResource(id = R.color.lightBlueGray)),
                contentAlignment = Alignment.Center
            ) {
                CupertinoIcon(
                    imageVector = centerIcon,
                    tint = activeColor,
                    contentDescription = null,
                    modifier = Modifier.size(centerIconSize)
                )
            }
        }
    }
}

// Helper: Create the underlying PieChart
fun createPieChart(
    context: Context,
    percentage: Float,
    activeColor: Color,
    inactiveColor: Color,
    holeRadius: Float
): PieChart {
    return PieChart(context).apply {
        description.isEnabled = false
        setUsePercentValues(false)
        isDrawHoleEnabled = true
        setHoleColor(android.graphics.Color.TRANSPARENT)
        setHoleRadius(holeRadius)
        transparentCircleRadius = 0f
        isClickable = false
        isHighlightPerTapEnabled = false
        setTouchEnabled(false)
        setDrawEntryLabels(false)

        legend.isEnabled = false

        val entries = buildPieEntries(percentage)
        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                activeColor.toArgb(),
                inactiveColor.toArgb()
            )
            setDrawValues(false)
        }

        data = PieData(dataSet)
        invalidate()
    }
}

// Helper: Create two entries: active portion & remainder.
fun buildPieEntries(percentage: Float): List<PieEntry> {
    val active = (percentage.coerceIn(0f, 1f) * 100)
    val remainder = 100f - active
    return listOf(
        PieEntry(active, ""),
                PieEntry(remainder, "")
    )
}
