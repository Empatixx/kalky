package cz.krokviak.kalky.scenes.home.components

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
import cz.krokviak.kalky.core.common.localizedName
import cz.krokviak.kalky.core.common.shortName
import cz.krokviak.kalky.core.common.withDayOfWeek
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.core.ui.LocalDimensions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeekDatePicker(
    currentDate: LocalDate,
    isToday: Boolean,
    onDateChange: (LocalDate) -> Unit,
    onTodayClick: () -> Unit
) {
    val mondayOfThisWeek = remember { currentDate.withDayOfWeek(DayOfWeek.MONDAY) }

    val days = remember {
        val initialStart = mondayOfThisWeek.minus(30, DateTimeUnit.DAY)
        val initialEnd = mondayOfThisWeek.plus(30, DateTimeUnit.DAY)
        generateDateRange(initialStart, initialEnd).toMutableStateList()
    }

    var selectedIndex by remember {
        mutableStateOf(days.indexOf(currentDate).coerceAtLeast(0))
    }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        val index = days.indexOf(currentDate).coerceAtLeast(0)
        listState.scrollToItem((index - 3).coerceAtLeast(0))
    }

    LaunchedEffect(currentDate) {
        val index = days.indexOf(currentDate)
        if (index >= 0 && index != selectedIndex) {
            selectedIndex = index
            listState.animateScrollToItem((index - 3).coerceAtLeast(0))
        }
    }

    observeInfiniteScroll(
        listState = listState,
        days = days,
        selectedIndexUpdater = { selectedIndex += it },
        coroutineScope = coroutineScope
    )
    MonthHeader(currentDate = currentDate, isToday = isToday, onTodayClick = onTodayClick)

    val dims = LocalDimensions.current
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val columnsToShow = 7
        val spacing = dims.halfSpacing
        val totalSpacing = spacing * (columnsToShow - 1)
        val itemWidth = (maxWidth - totalSpacing).coerceAtLeast(0.dp) / columnsToShow
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dims.itemSpacing),
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
    val monthName = currentDate.month.localizedName(LocalStrings.current.date)
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
                contentDescription = LocalStrings.current.home.today,
                tint = AppTheme.colors.onBackground
            )
        }
    }
}

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
        Text(
            text = date.dayOfMonth.toString(),
            fontWeight = FontWeight.ExtraBold,
            color = if (isSelected) AppTheme.colors.onPrimary else AppTheme.colors.onBackground,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(4.dp))

        Text(
            text = date.dayOfWeek.shortName(LocalStrings.current.date),
            color = if (isSelected) AppTheme.colors.onPrimary else AppTheme.colors.onBackgroundSecondary,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(4.dp))

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

                if (firstVisible < 5) {
                    prependMoreDays(days, coroutineScope, listState, firstVisible)

                    selectedIndexUpdater(30)
                }

                if (lastVisible > days.size - 5) {
                    appendMoreDays(days)
                }
            }
    }
}

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
        listState.scrollToItem(firstVisibleIndex + newDates.size)
    }
}

private fun appendMoreDays(days: MutableList<LocalDate>) {
    val lastDate = days.last()
    val newEnd = lastDate.plus(30, DateTimeUnit.DAY)
    val newDates = generateDateRange(lastDate.plus(1, DateTimeUnit.DAY), newEnd)
    days.addAll(newDates)
}
