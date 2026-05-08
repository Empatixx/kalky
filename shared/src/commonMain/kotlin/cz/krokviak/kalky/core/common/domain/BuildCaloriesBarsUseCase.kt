package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.scenes.analytics.CaloriesBar
import cz.krokviak.kalky.core.common.repo.FoodRepository
import cz.krokviak.kalky.core.common.shortName
import cz.krokviak.kalky.core.i18n.CzechStrings
import cz.krokviak.kalky.core.i18n.DateStrings
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

open class BuildCaloriesBarsUseCase(
    private val foodRepository: FoodRepository,
) {
    open suspend operator fun invoke(
        start: LocalDate,
        end: LocalDate,
        dateStrings: DateStrings = CzechStrings.date,
    ): PersistentList<CaloriesBar> {
        val days = daysBetween(start, end)
        val dailyTotals = foodRepository.getDailyMacroTotalsInRange(
            start.toString(),
            end.toString(),
        )
        val totalsByDate = dailyTotals.associateBy { it.day }

        return (0 until days).map { i ->
            val date = start.plus(i, DateTimeUnit.DAY)
            val label = if (days <= 14) {
                date.dayOfWeek.shortName(dateStrings)
            } else {
                "${date.dayOfMonth}.${date.monthNumber}."
            }
            val dayTotals = totalsByDate[date]
            CaloriesBar(
                label = label,
                protein = dayTotals?.totalProtein ?: 0,
                carbs = dayTotals?.totalCarbs ?: 0,
                fat = dayTotals?.totalFat ?: 0,
            )
        }.toPersistentList()
    }

    private fun daysBetween(start: LocalDate, end: LocalDate): Int =
        (end.toEpochDays() - start.toEpochDays() + 1).coerceAtLeast(1)
}
