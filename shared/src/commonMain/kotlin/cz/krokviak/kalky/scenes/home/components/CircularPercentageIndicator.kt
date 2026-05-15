package cz.krokviak.kalky.scenes.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CircularPercentageIndicator(
    percentage: Int,
    progressColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier.size(80.dp)
) {

    val progress = (percentage.coerceIn(0, 100)) / 100f

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {

            val canvasSize = size.minDimension
            val strokeWidth = canvasSize / 10f
            val radius = (canvasSize - strokeWidth) / 2
            val center = Offset(x = size.width / 2, y = size.height / 2)

            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )

            val sweepAngle = 360f * progress
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )
        }

        Text(
            text = "${percentage.coerceIn(0, 100)}%",
            color = progressColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
