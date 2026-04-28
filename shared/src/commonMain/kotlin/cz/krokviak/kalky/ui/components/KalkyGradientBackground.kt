package cz.krokviak.kalky.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.theme.KalkyAccents

@Composable
fun KalkyGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val leftTopCenter = Offset(x = size.width * 0.08f, y = size.height * 0.06f)
            val rightTopCenter = Offset(x = size.width * 1.04f, y = size.height * 0.02f)
            val leftTopRadius = size.minDimension * 0.56f
            val rightTopRadius = size.minDimension * 0.64f
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        KalkyAccents.brandSky.copy(alpha = 0.09f),
                        Color.Transparent
                    ),
                    center = leftTopCenter,
                    radius = leftTopRadius
                )
            )
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        KalkyAccents.brandViolet.copy(alpha = 0.20f),
                        KalkyAccents.brandPink.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = rightTopCenter,
                    radius = rightTopRadius
                )
            )
        }
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
