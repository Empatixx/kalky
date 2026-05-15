package cz.krokviak.kalky.core.common

import cz.krokviak.kalky.core.i18n.DateStrings
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
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
    val currentOrdinal = this.dayOfWeek.ordinal
    val targetOrdinal = target.ordinal
    val diff = targetOrdinal - currentOrdinal
    return this.plus(diff, DateTimeUnit.DAY)
}

fun DayOfWeek.shortName(strings: DateStrings): String =
    strings.daysShort.getOrElse(this.ordinal) { "" }

fun Month.localizedName(strings: DateStrings): String =
    strings.months.getOrElse(this.ordinal) { "" }
