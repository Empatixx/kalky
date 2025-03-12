package cz.krokviak.kalai.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.section.CupertinoSection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DatePicker() {
    // 1) "Today"
    val today = remember { LocalDate.now() }

    // 2) Monday of the current week
    val mondayOfThisWeek = remember { today.with(DayOfWeek.MONDAY) }

    // 3) We'll keep a mutable list of dates (initially ±30 days around that Monday).
    val days = remember {
        val initialStart = mondayOfThisWeek.minusDays(30)
        val initialEnd   = mondayOfThisWeek.plusDays(30)
        generateDateRange(initialStart, initialEnd).toMutableStateList()
    }

    // 4) Track which index is selected (start with Monday of this week).
    var selectedIndex by remember { mutableStateOf(days.indexOf(mondayOfThisWeek)) }

    // 5) Our lazy list state
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 6) Infinite scroll logic (optional)
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { firstVisible ->
                val visibleCount = listState.layoutInfo.visibleItemsInfo.size
                val lastVisible  = firstVisible + visibleCount

                // If near the left edge, prepend more days
                if (firstVisible < 5) {
                    prependMoreDays(days, coroutineScope, listState, firstVisible)
                    // Adjust selectedIndex if needed (we inserted 30 days at the start).
                    selectedIndex += 30
                }

                // If near the right edge, append more days
                if (lastVisible > days.size - 5) {
                    appendMoreDays(days)
                }
            }
    }

    // Wrap in BoxWithConstraints to get maxWidth
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        // We want exactly 7 columns fully visible
        val columnsToShow = 7
        val spacing = 8.dp
        // Between 7 columns there are 6 "gaps"
        val totalSpacing = spacing * (columnsToShow - 1)

        // Calculate each item’s width so that 7 items + 6 spacers fill maxWidth
        val itemWidth = (maxWidth - totalSpacing).coerceAtLeast(0.dp) / columnsToShow

        CupertinoSection(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent,
            contentPadding = PaddingValues(0.dp)
        ) {
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
                    val isSelected = (index == selectedIndex)

                    Column(
                        modifier = Modifier
                            // Use our computed itemWidth to ensure exactly 7 items fit
                            .width(itemWidth)
                            .background(
                                color = if (isSelected) Color.Black else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                // On click, scroll so that Monday is at leftmost
                                val mondayOfWeek = date.with(DayOfWeek.MONDAY)
                                val mondayIndex = days.indexOf(mondayOfWeek)
                                if (mondayIndex != -1) {
                                    selectedIndex = index
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(mondayIndex)
                                    }
                                }
                            }
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
                                .background(
                                    if (isSelected) Color.White else Color.Transparent
                                )
                        )
                    }
                }
            }
        }
    }
}

/** Generate a list of [LocalDate] from [start] to [end] inclusive. */
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
 * Prepend 30 more days to the front, then shift scroll so user sees the same item.
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
        // Shift scroll by the number of new items inserted
        listState.scrollToItem(firstVisibleIndex + newDates.size)
    }
}

/** Append 30 more days at the end. */
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
        DayOfWeek.MONDAY    -> "Po"
        DayOfWeek.TUESDAY   -> "Út"
        DayOfWeek.WEDNESDAY -> "St"
        DayOfWeek.THURSDAY  -> "Čt"
        DayOfWeek.FRIDAY    -> "Pá"
        DayOfWeek.SATURDAY  -> "So"
        DayOfWeek.SUNDAY    -> "Ne"
    }
