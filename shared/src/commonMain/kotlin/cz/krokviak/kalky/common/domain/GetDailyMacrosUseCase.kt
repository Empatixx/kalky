package cz.krokviak.kalky.common.domain

import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.common.repo.FoodRepository
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
        val caloriesDeferred = async { foodRepository.getTotalCaloriesForDate(dateStr) }
        val proteinDeferred = async { foodRepository.getTotalProteinForDate(dateStr) }
        val carbsDeferred = async { foodRepository.getTotalCarbsForDate(dateStr) }
        val fatsDeferred = async { foodRepository.getTotalFatsForDate(dateStr) }
        DailyMacros(
            items = itemsDeferred.await().toPersistentList(),
            totalCalories = caloriesDeferred.await(),
            totalProtein = proteinDeferred.await(),
            totalCarbs = carbsDeferred.await(),
            totalFat = fatsDeferred.await(),
        )
    }
}
