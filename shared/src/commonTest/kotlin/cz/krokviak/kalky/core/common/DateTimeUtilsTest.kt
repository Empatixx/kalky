package cz.krokviak.kalky.core.common

import cz.krokviak.kalky.core.i18n.CzechStrings
import cz.krokviak.kalky.core.i18n.EnglishStrings
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeUtilsTest {

    @Test
    fun shortName_picksCzechDay() {
        val cs = CzechStrings.date
        assertEquals("Po", DayOfWeek.MONDAY.shortName(cs))
        assertEquals("Út", DayOfWeek.TUESDAY.shortName(cs))
        assertEquals("Ne", DayOfWeek.SUNDAY.shortName(cs))
    }

    @Test
    fun shortName_picksEnglishDay() {
        val en = EnglishStrings.date
        assertEquals("Mon", DayOfWeek.MONDAY.shortName(en))
        assertEquals("Sun", DayOfWeek.SUNDAY.shortName(en))
    }

    @Test
    fun localizedName_picksCzechMonth() {
        val cs = CzechStrings.date
        assertEquals("Leden", Month.JANUARY.localizedName(cs))
        assertEquals("Březen", Month.MARCH.localizedName(cs))
        assertEquals("Prosinec", Month.DECEMBER.localizedName(cs))
    }

    @Test
    fun localizedName_picksEnglishMonth() {
        val en = EnglishStrings.date
        assertEquals("January", Month.JANUARY.localizedName(en))
        assertEquals("December", Month.DECEMBER.localizedName(en))
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
