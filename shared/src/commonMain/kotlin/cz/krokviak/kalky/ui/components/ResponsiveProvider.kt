package cz.krokviak.kalky.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.krokviak.kalky.ui.LocalDimensions
import cz.krokviak.kalky.ui.ScreenSizeClass
import cz.krokviak.kalky.ui.compactDimensions
import cz.krokviak.kalky.ui.largeDimensions
import cz.krokviak.kalky.ui.normalDimensions

@Composable
fun ResponsiveProvider(content: @Composable () -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val sizeClass = when {
            maxWidth < 360.dp -> ScreenSizeClass.Compact
            maxWidth <= 411.dp -> ScreenSizeClass.Normal
            else -> ScreenSizeClass.Large
        }
        val dimensions = when (sizeClass) {
            ScreenSizeClass.Compact -> compactDimensions
            ScreenSizeClass.Normal -> normalDimensions
            ScreenSizeClass.Large -> largeDimensions
        }
        CompositionLocalProvider(LocalDimensions provides dimensions) {
            content()
        }
    }
}
