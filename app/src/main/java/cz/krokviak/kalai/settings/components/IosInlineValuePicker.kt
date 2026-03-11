package cz.krokviak.kalai.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.theme.AppTheme
import kotlinx.coroutines.flow.distinctUntilChanged

private val ItemHeight = 36.dp
private val PickerHeight = ItemHeight * 5

@Composable
fun IosInlineValuePicker(
    values: List<String>,
    selectedIndex: Int,
    onIndexChanged: (Int) -> Unit,
    onCancel: () -> Unit,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Zrušit",
                color = AppTheme.colors.onBackgroundSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = onCancel
                )
            )
            Text(
                text = "Hotovo",
                color = AppTheme.colors.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = onDone
                )
            )
        }

        NumberWheel(
            values = values,
            initialIndex = selectedIndex.coerceIn(0, values.lastIndex),
            onIndexChanged = onIndexChanged
        )
    }
}

@Composable
private fun NumberWheel(
    values: List<String>,
    initialIndex: Int,
    onIndexChanged: (Int) -> Unit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapBehavior = rememberSnapFlingBehavior(listState)
    val surfaceColor = AppTheme.colors.surface
    val textColor = AppTheme.colors.onBackground

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index -> onIndexChanged(index.coerceIn(0, values.lastIndex)) }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(PickerHeight)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(ItemHeight)
                .background(
                    color = AppTheme.colors.onBackground.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(8.dp)
                )
        )

        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            contentPadding = PaddingValues(vertical = ItemHeight * 2),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(values) { _, value ->
                Box(
                    modifier = Modifier
                        .height(ItemHeight)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = value,
                        color = textColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(ItemHeight * 2)
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
                .height(ItemHeight * 2)
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
