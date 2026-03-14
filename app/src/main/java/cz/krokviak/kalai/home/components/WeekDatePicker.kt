package cz.krokviak.kalai.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.krokviak.kalai.common.toCzechShortName
import cz.krokviak.kalai.common.withDayOfWeek
import cz.krokviak.kalai.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/**
 * A horizontally scrollable date picker that:
 * - Always shows exactly 7 columns (days) at a time.
 * - Automatically scrolls so that the Monday of the current week is left-aligned at first composition.
 * - Highlights today's date by default.
 * - Supports "infinite" scroll by prepending or appending blocks of 30 days.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeekDatePicker(
    currentDate: LocalDate,
    isToday: Boolean,
    onDateChange: (LocalDate) -> Unit,
    onTodayClick: () -> Unit
) {
    // 2) Monday of the current week
    val mondayOfThisWeek = remember { currentDate.withDayOfWeek(DayOfWeek.MONDAY) }

    // 3) Build a mutable list of dates (±30 days around Monday).
    val days = remember {
        val initialStart = mondayOfThisWeek.minus(30, DateTimeUnit.DAY)
        val initialEnd = mondayOfThisWeek.plus(30, DateTimeUnit.DAY)
        generateDateRange(initialStart, initialEnd).toMutableStateList()
    }

    // 4) Track which index is "selected." Default is "today."
    var selectedIndex by remember {
        mutableStateOf(days.indexOf(currentDate).coerceAtLeast(0))
    }
    // 5) Lazy list state + coroutine scope for smooth scrolling
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    // 6) On first composition, scroll so current date is centered
    LaunchedEffect(Unit) {
        val index = days.indexOf(currentDate).coerceAtLeast(0)
        listState.scrollToItem((index - 3).coerceAtLeast(0))
    }

    // 7) Scroll back when currentDate changes externally (e.g. "back to today")
    LaunchedEffect(currentDate) {
        val index = days.indexOf(currentDate)
        if (index >= 0 && index != selectedIndex) {
            selectedIndex = index
            listState.animateScrollToItem((index - 3).coerceAtLeast(0))
        }
    }

    // 8) Observe lazy list edges, loading more days if needed
    observeInfiniteScroll(
        listState = listState,
        days = days,
        selectedIndexUpdater = { selectedIndex += it },  // shift selected index if needed
        coroutineScope = coroutineScope
    )
    MonthHeader(currentDate = currentDate, isToday = isToday, onTodayClick = onTodayClick)

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val columnsToShow = 7
        val spacing = 8.dp
        // 6 gaps between 7 columns
        val totalSpacing = spacing * (columnsToShow - 1)
        // Each item's width = (availableWidth - spacing) / columns
        val itemWidth = (maxWidth - totalSpacing).coerceAtLeast(0.dp) / columnsToShow
        // Horizontal scroller
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            itemsIndexed(days) { index, date ->
                DayItem(
                    date = date,
                    isSelected = index == selectedIndex,
                    itemWidth = itemWidth,
                    onDayClick = {
                        selectedIndex = index
                        coroutineScope.launch {
                            listState.animateScrollToItem((index - 3).coerceAtLeast(0))
                        }
                        onDateChange(it)
                    }
                )
            }
        }
    }
}


@Composable
fun MonthHeader(currentDate: LocalDate, isToday: Boolean, onTodayClick: () -> Unit) {
    val monthName = getNominativeMonthName(currentDate.month)
    val iconAlpha by animateFloatAsState(
        targetValue = if (isToday) 0f else 1f,
        label = "todayIconAlpha"
    )
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${currentDate.dayOfMonth}. $monthName ${currentDate.year}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = AppTheme.colors.onBackground
        )
        IconButton(
            onClick = onTodayClick,
            enabled = !isToday,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .alpha(iconAlpha)
        ) {
            Icon(
                imageVector = Icons.Filled.Restore,
                contentDescription = "Dnes",
                tint = AppTheme.colors.onBackground
            )
        }
    }
}




/**
 * Composable for a single day "column" item.
 *
 * @param date the date to display
 * @param isSelected whether this date is highlighted
 * @param itemWidth the fixed width for this column
 * @param onDayClick callback when the user clicks this date
 */
@Composable
private fun DayItem(
    date: LocalDate,
    isSelected: Boolean,
    itemWidth: Dp,
    onDayClick: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .width(itemWidth)
            .background(
                color = if (isSelected) AppTheme.colors.primary else Color.Transparent,
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDayClick(date) }
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Day number
        Text(
            text = date.dayOfMonth.toString(),
            fontWeight = FontWeight.ExtraBold,
            color = if (isSelected) AppTheme.colors.onPrimary else AppTheme.colors.onBackground,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(4.dp))

        // Short Czech name for the day
        Text(
            text = date.dayOfWeek.toCzechShortName(),
            color = if (isSelected) AppTheme.colors.onPrimary else AppTheme.colors.onBackgroundSecondary,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(4.dp))

        // Dot placeholder
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .then(
                    if (isSelected) {
                        Modifier.background(AppTheme.colors.onPrimary)
                    } else {
                        Modifier
                            .border(1.dp, AppTheme.colors.onBackground, CircleShape)
                            .background(Color.Transparent)
                    }
                )
        )
    }
}


/**
 * Observe the first visible item in the [LazyListState].
 * - If near the left edge, prepend days.
 * - If near the right edge, append days.
 * - Adjust [selectedIndexUpdater] if new items are prepended (so the selected day doesn't shift).
 */
@Composable
private fun observeInfiniteScroll(
    listState: LazyListState,
    days: MutableList<LocalDate>,
    selectedIndexUpdater: (Int) -> Unit,
    coroutineScope: CoroutineScope
) {
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collectLatest { firstVisible ->
                val visibleCount = listState.layoutInfo.visibleItemsInfo.size
                val lastVisible = firstVisible + visibleCount

                // If near the left edge, prepend more days
                if (firstVisible < 5) {
                    prependMoreDays(days, coroutineScope, listState, firstVisible)
                    // We inserted 30 days at the start, so shift the selection index by +30
                    selectedIndexUpdater(30)
                }

                // If near the right edge, append more days
                if (lastVisible > days.size - 5) {
                    appendMoreDays(days)
                }
            }
    }
}

/**
 * Generate a list of [LocalDate] from [start] to [end] (inclusive).
 */
private fun generateDateRange(start: LocalDate, end: LocalDate): List<LocalDate> {
    require(end >= start) { "End date can't be before start date." }
    val result = mutableListOf<LocalDate>()
    var cursor = start
    while (cursor <= end) {
        result += cursor
        cursor = cursor.plus(1, DateTimeUnit.DAY)
    }
    return result
}

/**
 * Prepend 30 more days to the start of [days],
 * then adjust the visible item to compensate,
 * so the user doesn't notice a jump.
 */
private fun prependMoreDays(
    days: MutableList<LocalDate>,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
    firstVisibleIndex: Int
) {
    val firstDate = days.first()
    val newStart = firstDate.minus(30, DateTimeUnit.DAY)
    val newDates = generateDateRange(newStart, firstDate.minus(1, DateTimeUnit.DAY))
    days.addAll(0, newDates)

    coroutineScope.launch {
        // Shift scroll to preserve the current visible position
        listState.scrollToItem(firstVisibleIndex + newDates.size)
    }
}

/**
 * Append 30 more days to the end of [days].
 */
private fun appendMoreDays(days: MutableList<LocalDate>) {
    val lastDate = days.last()
    val newEnd = lastDate.plus(30, DateTimeUnit.DAY)
    val newDates = generateDateRange(lastDate.plus(1, DateTimeUnit.DAY), newEnd)
    days.addAll(newDates)
}

fun getNominativeMonthName(month: Month): String {
    return when(month) {
        Month.JANUARY -> "Leden"
        Month.FEBRUARY -> "Únor"
        Month.MARCH -> "Březen"
        Month.APRIL -> "Duben"
        Month.MAY -> "Květen"
        Month.JUNE -> "Červen"
        Month.JULY -> "Červenec"
        Month.AUGUST -> "Srpen"
        Month.SEPTEMBER -> "Září"
        Month.OCTOBER -> "Říjen"
        Month.NOVEMBER -> "Listopad"
        Month.DECEMBER -> "Prosinec"
        else -> ""
    }
}
