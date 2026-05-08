package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.repo.FoodRepository

/** Deletes a single food item. Use [DeleteFoodItemsUseCase] for bulk deletion. */
open class DeleteFoodItemUseCase(
    private val foodRepository: FoodRepository,
) {
    open suspend operator fun invoke(id: Long) {
        foodRepository.deleteFoodItem(id)
    }
}
