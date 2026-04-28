package cz.krokviak.kalky.common.domain

import cz.krokviak.kalky.common.repo.FoodRepository

/** Deletes a single food item. Use [DeleteFoodItemsUseCase] for bulk deletion. */
class DeleteFoodItemUseCase(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(id: Long) {
        foodRepository.deleteFoodItem(id)
    }
}
