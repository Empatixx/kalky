package cz.krokviak.kalai.common

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

fun currentInstant(): Instant = Clock.System.now()

fun currentLocalDate(): LocalDate =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

fun Instant.formatTime(): String {
    val ldt = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${ldt.hour.toString().padStart(2, '0')}:${ldt.minute.toString().padStart(2, '0')}"
}

fun Instant.formatIsoDate(): String {
    val ldt = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return ldt.date.toString()
}

fun LocalDate.withDayOfWeek(target: DayOfWeek): LocalDate {
    val currentOrdinal = this.dayOfWeek.ordinal // Monday=0, Sunday=6
    val targetOrdinal = target.ordinal
    val diff = targetOrdinal - currentOrdinal
    return this.plus(diff, DateTimeUnit.DAY)
}

fun DayOfWeek.toCzechShortName(): String = when (this) {
    DayOfWeek.MONDAY -> "Po"
    DayOfWeek.TUESDAY -> "Út"
    DayOfWeek.WEDNESDAY -> "St"
    DayOfWeek.THURSDAY -> "Čt"
    DayOfWeek.FRIDAY -> "Pá"
    DayOfWeek.SATURDAY -> "So"
    DayOfWeek.SUNDAY -> "Ne"
    else -> "??"
}

fun Month.toCzechName(): String = when (this) {
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
