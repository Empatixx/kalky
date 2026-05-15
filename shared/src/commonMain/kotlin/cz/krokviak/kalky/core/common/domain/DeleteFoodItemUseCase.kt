package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.repo.FoodRepository

open class DeleteFoodItemUseCase(
    private val foodRepository: FoodRepository,
) {
    open suspend operator fun invoke(id: Long) {
        foodRepository.deleteFoodItem(id)
    }
}
