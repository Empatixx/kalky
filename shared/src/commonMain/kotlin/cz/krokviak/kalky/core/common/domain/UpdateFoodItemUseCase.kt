package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.repo.FoodRepository

class UpdateFoodItemUseCase(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(item: FoodItemEntity) {
        foodRepository.updateFoodItem(item)
    }
}
