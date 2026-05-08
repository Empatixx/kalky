package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.repo.FoodRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async
import kotlinx.datetime.LocalDate

data class DailyMacros(
    val items: PersistentList<FoodItemEntity> = persistentListOf(),
    val totalCalories: Int = 0,
    val totalProtein: Int = 0,
    val totalCarbs: Int = 0,
    val totalFat: Int = 0,
)

class GetDailyMacrosUseCase(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(date: LocalDate): DailyMacros = coroutineScope {
        val dateStr = date.toString()
        val itemsDeferred = async { foodRepository.getFoodItemsForDate(dateStr) }
        val totalsDeferred = async { foodRepository.getMacroTotalsForDate(dateStr) }
        val totals = totalsDeferred.await()
        DailyMacros(
            items = itemsDeferred.await().toPersistentList(),
            totalCalories = totals.calories,
            totalProtein = totals.protein,
            totalCarbs = totals.carbs,
            totalFat = totals.fat,
        )
    }
}
