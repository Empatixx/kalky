package cz.krokviak.kalky.common

import cz.krokviak.kalky.i18n.DateStrings
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
    val currentOrdinal = this.dayOfWeek.ordinal // Monday=0, Sunday=6
    val targetOrdinal = target.ordinal
    val diff = targetOrdinal - currentOrdinal
    return this.plus(diff, DateTimeUnit.DAY)
}

/**
 * DayOfWeek.ordinal: Mon=0..Sun=6 — matches DateStrings.daysShort indexing.
 */
fun DayOfWeek.shortName(strings: DateStrings): String =
    strings.daysShort.getOrElse(this.ordinal) { "" }

/**
 * Month.ordinal: January=0..December=11 — matches DateStrings.months indexing.
 */
fun Month.localizedName(strings: DateStrings): String =
    strings.months.getOrElse(this.ordinal) { "" }
