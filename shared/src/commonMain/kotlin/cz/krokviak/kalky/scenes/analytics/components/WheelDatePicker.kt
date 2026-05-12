package cz.krokviak.kalky.scenes.analytics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.core.theme.AppTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

private val ITEM_HEIGHT = 36.dp
private val PICKER_HEIGHT = ITEM_HEIGHT * 5  // 180.dp

private val czechMonths = listOf(
    "Leden", "Únor", "Březen", "Duben", "Květen", "Červen",
    "Červenec", "Srpen", "Září", "Říjen", "Listopad", "Prosinec"
)

private fun daysInMonth(year: Int, month: Int): Int {
    val firstOfNextMonth = if (month == 12) {
        LocalDate(year + 1, 1, 1)
    } else {
        LocalDate(year, month + 1, 1)
    }
    return firstOfNextMonth.minus(1, DateTimeUnit.DAY).dayOfMonth
}

@Composable
fun WheelDatePickerInline(
    initialDate: LocalDate,
    onDateChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
    val yearStart = today.year - 5
    val years = remember { (yearStart..(today.year + 1)).toList() }

    var selectedYear by remember { mutableStateOf(initialDate.year) }
    var selectedMonth by remember { mutableStateOf(initialDate.monthNumber) }
    var selectedDay by remember { mutableStateOf(initialDate.dayOfMonth) }

    val maxDays = remember(selectedYear, selectedMonth) {
        daysInMonth(selectedYear, selectedMonth)
    }

    LaunchedEffect(maxDays) {
        if (selectedDay > maxDays) selectedDay = maxDays
    }

    LaunchedEffect(selectedDay, selectedMonth, selectedYear, maxDays) {
        val clampedDay = selectedDay.coerceIn(1, maxDays)
        val newDate = LocalDate(selectedYear, selectedMonth, clampedDay)
        // skip the no-op emit on first composition (would trigger a needless reload)
        if (newDate != initialDate) onDateChanged(newDate)
    }

    val surfaceColor = AppTheme.colors.surface
    val indicatorColor = AppTheme.colors.onBackground.copy(alpha = 0.06f)
    val textColor = AppTheme.colors.onBackground

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(PICKER_HEIGHT)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(ITEM_HEIGHT)
                .padding(horizontal = 8.dp)
                .background(
                    color = indicatorColor,
                    shape = RoundedCornerShape(8.dp)
                )
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            key(maxDays) {
                WheelColumn(
                    items = (1..maxDays).map { it.toString() },
                    initialIndex = (selectedDay - 1).coerceIn(0, maxDays - 1),
                    onIndexChanged = { selectedDay = it + 1 },
                    textColor = textColor,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.8f)
                )
            }
            WheelColumn(
                items = czechMonths,
                initialIndex = selectedMonth - 1,
                onIndexChanged = { selectedMonth = it + 1 },
                textColor = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1.4f)
            )
            WheelColumn(
                items = years.map { it.toString() },
                initialIndex = (initialDate.year - yearStart).coerceIn(0, years.lastIndex),
                onIndexChanged = { selectedYear = yearStart + it },
                textColor = textColor,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(ITEM_HEIGHT * 2)
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
                .height(ITEM_HEIGHT * 2)
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

@Composable
private fun WheelColumn(
    items: List<String>,
    initialIndex: Int,
    onIndexChanged: (Int) -> Unit,
    textColor: Color,
    textAlign: TextAlign,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex
    )
    val snapBehavior = rememberSnapFlingBehavior(lazyListState)

    // report the index only once scrolling settles (every item crossed mid-fling would fire a reload)
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { !it }
            .collect { onIndexChanged(lazyListState.firstVisibleItemIndex) }
    }

    LazyColumn(
        state = lazyListState,
        flingBehavior = snapBehavior,
        contentPadding = PaddingValues(vertical = ITEM_HEIGHT * 2),
        modifier = modifier.height(PICKER_HEIGHT)
    ) {
        itemsIndexed(items) { _, item ->
            Box(
                modifier = Modifier
                    .height(ITEM_HEIGHT)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    color = textColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = textAlign,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
