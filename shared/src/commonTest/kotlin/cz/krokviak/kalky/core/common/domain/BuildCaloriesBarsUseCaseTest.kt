package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.repo.FoodRepository
import cz.krokviak.kalky.core.i18n.CzechStrings
import cz.krokviak.kalky.scenes.analytics.data.DailyMacroTotals
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class BuildCaloriesBarsUseCaseTest {

    @Test
    fun emptyRange_returnsOneEmptyBar_forSameStartAndEnd() = runTest {
        val repo = mock<FoodRepository> {
            everySuspend { getDailyMacroTotalsInRange(any(), any()) } returns emptyList()
        }
        val date = LocalDate(2026, 5, 8)

        val bars = BuildCaloriesBarsUseCase(repo).invoke(date, date)

        assertEquals(1, bars.size)
        assertEquals(0, bars.single().protein)
        assertEquals(0, bars.single().carbs)
        assertEquals(0, bars.single().fat)
    }

    @Test
    fun sevenDayRange_returnsSevenBars_withDayLabels() = runTest {
        val start = LocalDate(2026, 5, 1) // Friday
        val end = LocalDate(2026, 5, 7)
        val repo = mock<FoodRepository> {
            everySuspend { getDailyMacroTotalsInRange(any(), any()) } returns listOf(
                DailyMacroTotals(start, totalProtein = 50, totalCarbs = 100, totalFat = 30),
                DailyMacroTotals(end, totalProtein = 80, totalCarbs = 200, totalFat = 60),
            )
        }

        val bars = BuildCaloriesBarsUseCase(repo).invoke(start, end, CzechStrings.date)

        assertEquals(7, bars.size)
        assertEquals(50, bars.first().protein)
        assertEquals(80, bars.last().protein)
        // Days <= 14 -> short day names
        assertEquals(2, bars.first().label.length)
    }

    @Test
    fun longRange_usesNumericLabels_overFourteenDays() = runTest {
        val start = LocalDate(2026, 5, 1)
        val end = LocalDate(2026, 5, 20)
        val repo = mock<FoodRepository> {
            everySuspend { getDailyMacroTotalsInRange(any(), any()) } returns emptyList()
        }

        val bars = BuildCaloriesBarsUseCase(repo).invoke(start, end)

        assertEquals(20, bars.size)
        assertEquals("1.5.", bars.first().label)
        assertEquals("20.5.", bars.last().label)
    }

    @Test
    fun missingDateInTotals_yieldsZeroBar() = runTest {
        val start = LocalDate(2026, 5, 1)
        val end = LocalDate(2026, 5, 3)
        val repo = mock<FoodRepository> {
            everySuspend { getDailyMacroTotalsInRange(any(), any()) } returns listOf(
                DailyMacroTotals(LocalDate(2026, 5, 2), totalProtein = 10, totalCarbs = 20, totalFat = 5),
            )
        }

        val bars = BuildCaloriesBarsUseCase(repo).invoke(start, end)

        assertEquals(3, bars.size)
        assertEquals(0, bars[0].protein)  // 5/1 missing
        assertEquals(10, bars[1].protein) // 5/2
        assertEquals(0, bars[2].protein)  // 5/3 missing
    }
}
