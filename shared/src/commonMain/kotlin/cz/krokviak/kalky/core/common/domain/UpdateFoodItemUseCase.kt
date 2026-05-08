package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.repo.FoodRepository

open class UpdateFoodItemUseCase(
    private val foodRepository: FoodRepository,
) {
    open suspend operator fun invoke(item: FoodItemEntity) {
        foodRepository.updateFoodItem(item)
    }
}
