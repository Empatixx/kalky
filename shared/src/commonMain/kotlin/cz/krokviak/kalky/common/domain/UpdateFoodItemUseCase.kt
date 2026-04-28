package cz.krokviak.kalky.common.domain

import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.common.repo.FoodRepository

class UpdateFoodItemUseCase(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(item: FoodItemEntity) {
        foodRepository.updateFoodItem(item)
    }
}
