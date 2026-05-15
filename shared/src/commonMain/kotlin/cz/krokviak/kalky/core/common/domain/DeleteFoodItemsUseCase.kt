package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.repo.FoodRepository

open class DeleteFoodItemsUseCase(
    private val foodRepository: FoodRepository,
) {
    open suspend operator fun invoke(ids: Collection<Long>) {
        for (id in ids) {
            foodRepository.deleteFoodItem(id)
        }
    }
}
