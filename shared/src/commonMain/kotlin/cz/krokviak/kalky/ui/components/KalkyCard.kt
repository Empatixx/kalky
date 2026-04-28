package cz.krokviak.kalky.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.ui.LocalDimensions

@Composable
fun KalkyCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
    color: Color = AppTheme.colors.surface,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = color
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
