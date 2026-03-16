package cz.krokviak.kalai.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.ui.LocalDimensions

@Composable
fun KalaiSegmentedControl(
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
    val dims = LocalDimensions.current
    val shape = RoundedCornerShape(8.dp)

    Surface(
        modifier = modifier.height(dims.buttonHeight),
        shape = shape,
        color = trackColor
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Animated indicator
            if (items.isNotEmpty()) {
                val itemCount = items.size
                val fraction = 1f / itemCount

                Box(modifier = Modifier.fillMaxWidth()) {
                    val animatedOffset by animateDpAsState(
                        targetValue = with(LocalDensity.current) { 0.dp }, // placeholder, calculated via fraction
                        animationSpec = tween(200),
                        label = "indicator"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(36.dp)
                            .padding(2.dp)
                            .offset(x = with(LocalDensity.current) { 0.dp }) // We'll use fractional positioning
                            .align(Alignment.CenterStart)
                    )
                }

                // Row of items with indicator behind
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
                            // Indicator behind selected item
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
    }
}
