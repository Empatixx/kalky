package cz.krokviak.kalai.analytics.data

import kotlinx.datetime.LocalDate

data class DailyMacroTotals(
    val day: LocalDate,
    val totalProtein: Int?,
    val totalCarbs: Int?,
    val totalFat: Int?
)
