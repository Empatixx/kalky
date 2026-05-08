package cz.krokviak.kalky.core.camera.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CaptureButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .size(80.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
    ) {
        val strokeWidth = 4.dp.toPx()
        val outerRadius = (size.minDimension / 2f) - (strokeWidth / 2f)

        // Outer ring
        drawCircle(
            color = Color.White,
            radius = outerRadius,
            style = Stroke(width = strokeWidth)
        )
        // Inner circle
        drawCircle(
            color = Color.White,
            radius = size.minDimension / 2.5f
        )
    }
}
