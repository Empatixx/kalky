package cz.krokviak.kalky.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.ui.LocalDimensions

@Composable
fun KalkySegmentedControl(
    selectedIndex: Int,
    items: List<String>,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    trackColor: Color = AppTheme.colors.surfaceSecondary,
    indicatorColor: Color = AppTheme.colors.surface,
    selectedTextColor: Color = AppTheme.colors.onBackground,
    unselectedTextColor: Color = AppTheme.colors.onBackgroundSecondary,
    textSize: TextUnit = 16.sp,
) {
    if (items.isEmpty()) return
    val dims = LocalDimensions.current
    val shape = RoundedCornerShape(8.dp)

    Surface(
        modifier = modifier.height(dims.buttonHeight),
        shape = shape,
        color = trackColor
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            items.forEachIndexed { index, label ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(dims.buttonHeight)
                        .clip(shape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onItemSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    if (index == selectedIndex) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .padding(2.dp)
                                .background(indicatorColor, shape)
                        )
                    }
                    Text(
                        text = label,
                        fontSize = textSize,
                        fontWeight = FontWeight.SemiBold,
                        color = if (index == selectedIndex) selectedTextColor else unselectedTextColor
                    )
                }
            }
        }
    }
}
