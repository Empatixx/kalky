package cz.krokviak.kalai.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.section.CupertinoSection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.Locale

/**
 * A horizontally scrollable date picker that:
 * - Always shows exactly 7 columns (days) at a time.
 * - Automatically scrolls so that the Monday of the current week is left-aligned at first composition.
 * - Highlights today's date by default.
 * - Supports “infinite” scroll by prepending or appending blocks of 30 days.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeekDatePicker(
    currentDate: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    // 2) Monday of the current week
    val mondayOfThisWeek = remember { currentDate.with(DayOfWeek.MONDAY) }

    // 3) Build a mutable list of dates (±30 days around Monday).
    val days = remember {
        val initialStart = mondayOfThisWeek.minusDays(30)
        val initialEnd = mondayOfThisWeek.plusDays(30)
        generateDateRange(initialStart, initialEnd).toMutableStateList()
    }

    // 4) Track which index is "selected." Default is "today."
    var selectedIndex by remember {
        mutableStateOf(days.indexOf(currentDate).coerceAtLeast(0))
    }
    // 5) Lazy list state + coroutine scope for smooth scrolling
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    // 6) On first composition, scroll so MondayOfThisWeek is at left
    initListScroll(
        listState = listState,
        days = days,
        mondayOfThisWeek = mondayOfThisWeek
    )

    // 7) Observe lazy list edges, loading more days if needed
    observeInfiniteScroll(
        listState = listState,
        days = days,
        selectedIndexUpdater = { selectedIndex += it },  // shift selected index if needed
        coroutineScope = coroutineScope
    )
    MonthHeader(currentDate = currentDate)

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
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            itemsIndexed(days) { index, date ->
                DayItem(
                    date = date,
                    isSelected = index == selectedIndex,
                    itemWidth = itemWidth,
                    onDayClick = {
                        // Scroll so that the Monday of the clicked day is left-aligned
                        val mondayIndex = days.indexOf(it.with(DayOfWeek.MONDAY))
                        if (mondayIndex != -1) {
                            selectedIndex = index
                            coroutineScope.launch {
                                listState.animateScrollToItem(mondayIndex)
                            }
                        }
                        onDateChange(it)
                    }
                )
            }
        }
    }
}


@Composable
fun MonthHeader(currentDate: LocalDate) {
    val monthName = getNominativeMonthName(currentDate.month)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        CupertinoText(
            text = monthName,
            style = MaterialTheme.typography.titleLarge
        )
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
                color = if (isSelected) Color.Black else Color.Transparent,
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
        CupertinoText(
            text = date.dayOfMonth.toString(),
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(4.dp))

        // Short Czech name for the day
        CupertinoText(
            text = date.dayOfWeek.toCzechShortName(),
            color = if (isSelected) Color.White else Color.Gray,
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
                        Modifier.background(Color.White)
                    } else {
                        Modifier
                            .border(1.dp, Color.Black, CircleShape)
                            .background(Color.Transparent)
                    }
                )
        )
    }
}

/**
 * One-time initialization: scroll so that [mondayOfThisWeek] is left-aligned
 * in the horizontal list, if present in [days].
 */
@Composable
private fun initListScroll(
    listState: LazyListState,
    days: List<LocalDate>,
    mondayOfThisWeek: LocalDate
) {
    LaunchedEffect(Unit) {
        val mondayIndex = days.indexOf(mondayOfThisWeek)
        if (mondayIndex != -1) {
            listState.scrollToItem(mondayIndex)
        }
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
 *
 * @throws IllegalArgumentException if [end] is before [start].
 */
private fun generateDateRange(start: LocalDate, end: LocalDate): List<LocalDate> {
    require(!end.isBefore(start)) { "End date can't be before start date." }
    val result = mutableListOf<LocalDate>()
    var cursor = start
    while (!cursor.isAfter(end)) {
        result += cursor
        cursor = cursor.plusDays(1)
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
    val newStart = firstDate.minusDays(30)
    val newDates = generateDateRange(newStart, firstDate.minusDays(1))
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
    val newEnd = lastDate.plusDays(30)
    val newDates = generateDateRange(lastDate.plusDays(1), newEnd)
    days.addAll(newDates)
}

/**
 * Map a [DayOfWeek] to a short Czech name: “Po”, “Út”, “St”, “Čt”, “Pá”, “So”, “Ne”.
 */
private fun DayOfWeek.toCzechShortName(): String =
    when (this) {
        DayOfWeek.MONDAY -> "Po"
        DayOfWeek.TUESDAY -> "Út"
        DayOfWeek.WEDNESDAY -> "St"
        DayOfWeek.THURSDAY -> "Čt"
        DayOfWeek.FRIDAY -> "Pá"
        DayOfWeek.SATURDAY -> "So"
        DayOfWeek.SUNDAY -> "Ne"
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
    }
}