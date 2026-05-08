package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.repo.FoodRepository

/** Inserts a food item and returns its new database id. */
class AddFoodItemUseCase(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(item: FoodItemEntity): Long =
        foodRepository.insertFoodItem(item)
}
