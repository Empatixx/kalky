package cz.krokviak.kalky.common.domain

import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.common.repo.FoodRepository

class GetFoodItemUseCase(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(id: Long): FoodItemEntity? =
        foodRepository.getFoodItem(id)
}
