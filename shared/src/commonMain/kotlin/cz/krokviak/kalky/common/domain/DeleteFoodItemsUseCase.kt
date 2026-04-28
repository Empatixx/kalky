package cz.krokviak.kalky.common.domain

import cz.krokviak.kalky.common.repo.FoodRepository

/** Deletes a set of food items by id. */
class DeleteFoodItemsUseCase(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(ids: Collection<Long>) {
        for (id in ids) {
            foodRepository.deleteFoodItem(id)
        }
    }
}
