package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.repo.FoodRepository

open class AddFoodItemUseCase(
    private val foodRepository: FoodRepository,
) {
    open suspend operator fun invoke(item: FoodItemEntity): Long =
        foodRepository.insertFoodItem(item)
}
