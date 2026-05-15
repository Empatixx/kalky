package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.repo.FoodRepository
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.LocalDate

open class ObserveDailyMacrosUseCase(
    private val foodRepository: FoodRepository,
) {
    open operator fun invoke(date: LocalDate): Flow<DailyMacros> {
        val dateStr = date.toString()
        return combine(
            foodRepository.observeFoodItemsForDate(dateStr),
            foodRepository.observeMacroTotalsForDate(dateStr),
        ) { items, totals ->
            DailyMacros(
                items = items.toPersistentList(),
                totalCalories = totals.calories,
                totalProtein = totals.protein,
                totalCarbs = totals.carbs,
                totalFat = totals.fat,
            )
        }
    }
}
