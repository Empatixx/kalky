package cz.krokviak.kalky.scenes.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.core.theme.AppTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun IosInlineValuePicker(
    values: List<String>,
    selectedIndex: Int,
    onIndexChanged: (Int) -> Unit,
    unitSuffix: String? = null,
    itemHeight: Dp = 36.dp,
    visibleItemsCount: Int = 5,
    textSize: TextUnit = 20.sp,
    horizontalPadding: Dp = 12.dp,
    bottomPadding: Dp = 8.dp
) {
    val resolvedVisibleCount = visibleItemsCount
        .coerceAtLeast(3)
        .let { if (it % 2 == 0) it + 1 else it }
    val halfVisibleCount = resolvedVisibleCount / 2
    val pickerHeight = itemHeight * resolvedVisibleCount

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .padding(bottom = bottomPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NumberWheel(
            values = values,
            initialIndex = selectedIndex.coerceIn(0, values.lastIndex),
            onIndexChanged = onIndexChanged,
            unitSuffix = unitSuffix,
            itemHeight = itemHeight,
            pickerHeight = pickerHeight,
            halfVisibleCount = halfVisibleCount,
            textSize = textSize
        )
    }
}

@Composable
private fun NumberWheel(
    values: List<String>,
    initialIndex: Int,
    onIndexChanged: (Int) -> Unit,
    unitSuffix: String?,
    itemHeight: Dp,
    pickerHeight: Dp,
    halfVisibleCount: Int,
    textSize: TextUnit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapBehavior = rememberSnapFlingBehavior(listState)
    val surfaceColor = AppTheme.colors.surface
    val textColor = AppTheme.colors.onBackground

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { !it }
            .collect { onIndexChanged(listState.firstVisibleItemIndex.coerceIn(0, values.lastIndex)) }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(pickerHeight)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeight)
                .background(
                    color = AppTheme.colors.onBackground.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(8.dp)
                )
        )

        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            contentPadding = PaddingValues(vertical = itemHeight * halfVisibleCount),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(values) { _, value ->
                val displayValue = if (unitSuffix.isNullOrBlank()) value else "$value $unitSuffix"
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayValue,
                        color = textColor,
                        fontSize = textSize,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        val fadeHeight = itemHeight * halfVisibleCount.coerceAtLeast(1)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(fadeHeight)
                .background(
                    brush = Brush.verticalGradient(
                        0f to surfaceColor,
                        0.3f to surfaceColor.copy(alpha = 0.7f),
                        1f to Color.Transparent
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(fadeHeight)
                .background(
                    brush = Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.7f to surfaceColor.copy(alpha = 0.7f),
                        1f to surfaceColor
                    )
                )
        )
    }
}
