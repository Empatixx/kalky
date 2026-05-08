package cz.krokviak.kalky.core.common

import cz.krokviak.kalky.core.common.repo.FoodRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlin.test.Test
import kotlin.test.assertEquals

class StreakCalculatorTest {

    private val today = currentLocalDate()

    private fun mockRepo(dates: List<String>) = mock<FoodRepository> {
        everySuspend { getRecentDistinctFoodDates(any()) } returns dates
    }

    @Test
    fun emptyDates_returnsZero() = runTest {
        val streak = StreakCalculator(mockRepo(emptyList())).getCurrentStreak()
        assertEquals(0, streak)
    }

    @Test
    fun todayOnly_returnsOne() = runTest {
        val streak = StreakCalculator(mockRepo(listOf(today.toString()))).getCurrentStreak()
        assertEquals(1, streak)
    }

    @Test
    fun threeConsecutiveDaysIncludingToday_returnsThree() = runTest {
        val dates = (0..2).map { today.minus(it.toLong(), DateTimeUnit.DAY).toString() }
        val streak = StreakCalculator(mockRepo(dates)).getCurrentStreak()
        assertEquals(3, streak)
    }

    @Test
    fun yesterdayButNotToday_streakStartsAtYesterday() = runTest {
        val yesterday = today.minus(1, DateTimeUnit.DAY)
        val twoDaysAgo = today.minus(2, DateTimeUnit.DAY)
        val streak = StreakCalculator(
            mockRepo(listOf(yesterday.toString(), twoDaysAgo.toString()))
        ).getCurrentStreak()
        assertEquals(2, streak)
    }

    @Test
    fun gapInDates_streakStopsAtGap() = runTest {
        val dates = listOf(
            today.toString(),
            today.minus(1, DateTimeUnit.DAY).toString(),
            // gap on day -2
            today.minus(3, DateTimeUnit.DAY).toString(),
        )
        val streak = StreakCalculator(mockRepo(dates)).getCurrentStreak()
        assertEquals(2, streak)
    }

    @Test
    fun olderThanYesterday_returnsZero() = runTest {
        val twoDaysAgo = today.minus(2, DateTimeUnit.DAY)
        val streak = StreakCalculator(mockRepo(listOf(twoDaysAgo.toString()))).getCurrentStreak()
        assertEquals(0, streak)
    }

    @Test
    fun malformedDateStrings_areIgnored() = runTest {
        val dates = listOf(today.toString(), "not-a-date", "2026-13-99")
        val streak = StreakCalculator(mockRepo(dates)).getCurrentStreak()
        assertEquals(1, streak)
    }
}
