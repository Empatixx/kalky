package cz.krokviak.kalky.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.min
import cz.krokviak.kalky.theme.AppTheme
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
fun MacroNutrientDonutChart(
    modifier: Modifier = Modifier,
    percentage: Float,
    activeColor: Color,
    inactiveColor: Color = AppTheme.colors.surfaceSecondary,
    strokeWidthFraction: Float = 0.10f,
    centerIconBackgroundFraction: Float = 0.4f,
    centerIcon: ImageVector? = null,
    iconColor: Color = activeColor,
    iconFraction: Float = 0.2f,
    centerIconBackgroundColor: Color = AppTheme.colors.surfaceSecondary,
) {
    // BoxWithConstraints gives us maxWidth & maxHeight of the parent
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // The limiting dimension of this Box (e.g. if width < height, use width)
        val chartSize = min(maxWidth, maxHeight)

        // Convert that to a Dp to measure actual size
        val strokeWidth = chartSize * strokeWidthFraction

        // Use the computed chartSize for the entire PieChart
        // (You could also just do .fillMaxSize(), but for a donut typically you'd keep it a square.)
        Box(
            modifier = Modifier
                .size(chartSize),
            contentAlignment = Alignment.Center
        ) {
            // Prepare data
            val data by remember(percentage) {
                mutableStateOf(
                    listOf(
                        Pie(
                            label = "Filled",
                            data = percentage.toDouble() * 100,
                            color = activeColor
                        ),
                        Pie(
                            label = "Unfilled",
                            data = 100 - (percentage.toDouble() * 100),
                            color = inactiveColor
                        ),
                    )
                )
            }

            // Draw the PieChart
            PieChart(
                data = data,
                modifier = Modifier.fillMaxSize()
                    .rotate(degrees = 270f),
                selectedScale = 1.0f,
                style = Pie.Style.Stroke(
                    width = strokeWidth  // use dynamic stroke width
                )
            )

            // Center icon
            if (centerIcon != null) {
                // Option 1: Scale the background circle & icon relative to the chart size
                val iconBackgroundSize = chartSize * centerIconBackgroundFraction
                val iconSize = chartSize * iconFraction

                Box(
                    modifier = Modifier
                        .size(iconBackgroundSize)
                        .clip(CircleShape)
                        .background(centerIconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = centerIcon,
                        tint = iconColor,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }
    }
}
