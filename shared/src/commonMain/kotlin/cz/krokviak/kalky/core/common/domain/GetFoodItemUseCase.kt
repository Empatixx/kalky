package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.repo.FoodRepository

class GetFoodItemUseCase(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(id: Long): FoodItemEntity? =
        foodRepository.getFoodItem(id)
}
