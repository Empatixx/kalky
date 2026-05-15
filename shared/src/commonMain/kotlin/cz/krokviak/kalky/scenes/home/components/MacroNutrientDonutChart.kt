package cz.krokviak.kalky.scenes.home.components

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
import cz.krokviak.kalky.core.theme.AppTheme
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
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val chartSize = min(maxWidth, maxHeight)
        val strokeWidth = chartSize * strokeWidthFraction

        Box(
            modifier = Modifier
                .size(chartSize),
            contentAlignment = Alignment.Center
        ) {
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

            PieChart(
                data = data,
                modifier = Modifier.fillMaxSize()
                    .rotate(degrees = 270f),
                selectedScale = 1.0f,
                style = Pie.Style.Stroke(
                    width = strokeWidth
                )
            )

            if (centerIcon != null) {
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
