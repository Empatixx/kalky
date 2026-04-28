package cz.krokviak.kalky.common

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeUtilsTest {

    @Test
    fun toCzechShortName_allDays() {
        assertEquals("Po", DayOfWeek.MONDAY.toCzechShortName())
        assertEquals("Út", DayOfWeek.TUESDAY.toCzechShortName())
        assertEquals("St", DayOfWeek.WEDNESDAY.toCzechShortName())
        assertEquals("Čt", DayOfWeek.THURSDAY.toCzechShortName())
        assertEquals("Pá", DayOfWeek.FRIDAY.toCzechShortName())
        assertEquals("So", DayOfWeek.SATURDAY.toCzechShortName())
        assertEquals("Ne", DayOfWeek.SUNDAY.toCzechShortName())
    }

    @Test
    fun toCzechName_someMonths() {
        assertEquals("Leden", Month.JANUARY.toCzechName())
        assertEquals("Březen", Month.MARCH.toCzechName())
        assertEquals("Prosinec", Month.DECEMBER.toCzechName())
    }

    @Test
    fun withDayOfWeek_movesToTargetWithinSameWeek() {
        // 2026-04-28 is Tuesday
        val tuesday = LocalDate(2026, 4, 28)
        assertEquals(LocalDate(2026, 4, 27), tuesday.withDayOfWeek(DayOfWeek.MONDAY))
        assertEquals(LocalDate(2026, 4, 28), tuesday.withDayOfWeek(DayOfWeek.TUESDAY))
        assertEquals(LocalDate(2026, 5, 3), tuesday.withDayOfWeek(DayOfWeek.SUNDAY))
    }

    @Test
    fun withDayOfWeek_fromSundayCanGoBackToMonday() {
        // 2026-05-03 is Sunday
        val sunday = LocalDate(2026, 5, 3)
        // Monday=0, Sunday=6 → diff = 0 - 6 = -6 → 2026-04-27 (previous Monday)
        assertEquals(LocalDate(2026, 4, 27), sunday.withDayOfWeek(DayOfWeek.MONDAY))
    }
}
