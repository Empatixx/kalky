package cz.krokviak.kalai.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.section.CupertinoSection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate

/**
 * Creates a horizontally infinite “date picker,” centered initially on [today].
 * Scrolling left or right lazy‑loads more days in that direction.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DatePicker() {
    // 1) "Today"
    val today = remember { LocalDate.now() }

    // 2) We'll keep a mutable list of dates (initially ±30 days around today).
    val days = remember {
        val initialStart = today.minusDays(30)
        val initialEnd   = today.plusDays(30)
        generateDateRange(initialStart, initialEnd).toMutableStateList()
    }

    // 3) Track which index is selected (start with "today").
    var selectedIndex by remember { mutableStateOf(days.indexOf(today)) }

    // 4) Our lazy list state
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 5) Scroll to "today" on the first composition,
    //    then center that item exactly.
    LaunchedEffect(Unit) {
        // Phase 1: Basic scroll so the item is visible
        listState.scrollToItem(selectedIndex)

        // Phase 2: Once visible, measure the item and center it exactly.
        snapshotFlow {
            // Wait until the item is laid out
            listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == selectedIndex }
        }
            .filterNotNull()
            .take(1)  // only do once
            .collect { itemInfo ->
                // The center offset is half the viewport minus half the item width
                val viewportCenter = listState.layoutInfo.viewportSize.width / 2
                val itemCenter = itemInfo.size / 2
                // Where the item currently starts from the left
                val currentItemStart = itemInfo.offset
                // How far we need to scroll so the item’s center is at the viewport center
                val desiredOffset = (currentItemStart + itemCenter) - viewportCenter

                // Scroll by that difference
                listState.scrollBy(desiredOffset.toFloat())
            }
    }

    // 6) Whenever the visible list range changes, detect if we’re near ends → load more dates
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { firstVisible ->
                val visibleCount = listState.layoutInfo.visibleItemsInfo.size
                val lastVisible  = firstVisible + visibleCount

                // If near the left edge, prepend more days
                if (firstVisible < 5) {
                    prependMoreDays(days, coroutineScope, listState, firstVisible)
                    // Adjust selectedIndex so it still points to the same date
                    // (because we effectively inserted items at the start).
                    selectedIndex += 30 // We always add 30 days in prepend
                }

                // If near the right edge, append more days
                if (lastVisible > days.size - 5) {
                    appendMoreDays(days)
                }
            }
    }

    CupertinoSection(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        contentPadding = PaddingValues(0.dp)
    ) {
        // Horizontal infinite scroller
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentPadding = PaddingValues(horizontal = 50.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(days) { index, date ->
                // Is this date currently selected?
                val isSelected = (index == selectedIndex)

                Column(
                    modifier = Modifier
                        .width(50.dp)  // fixed width -> helps with centering
                        .fillMaxHeight()
                        .padding(vertical = 4.dp)
                        .background(
                            color = if (isSelected) Color.Black else Color.Transparent,
                            shape = CircleShape
                        )
                        .padding(vertical = 8.dp)
                        .clickable {
                            selectedIndex = index
                            coroutineScope.launch {
                                centerOnItem(listState, index)
                            }
                        },
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
                    // Short day name in Czech
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
                            .background(if (isSelected) Color.White else Color.Transparent)
                    )
                }
            }
        }
    }
}

/**
 * Generate a list of [LocalDate] from [start] to [end] inclusive.
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

/**
 * Append 30 more days at the end.
 */
private fun appendMoreDays(days: MutableList<LocalDate>) {
    val lastDate = days.last()
    val newEnd = lastDate.plusDays(30)
    val newDates = generateDateRange(lastDate.plusDays(1), newEnd)
    days.addAll(newDates)
}

/**
 * On click, center a new item in the viewport if possible.
 */
private suspend fun centerOnItem(listState: LazyListState, index: Int) {
    snapshotFlow {
        listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
    }.filterNotNull()
        .take(1)
        .collect { itemInfo ->
            val viewportCenter = listState.layoutInfo.viewportSize.width / 2
            val itemCenter = itemInfo.offset + (itemInfo.size * 3 / 2f) //wtf
            val distance = itemCenter - viewportCenter
            listState.animateScrollBy(distance)
        }
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
