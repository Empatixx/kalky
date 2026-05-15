package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.repo.FoodRepository

data class FoodLibrary(
    val custom: List<FoodItemEntity>,
    val history: List<FoodItemEntity>,
)

open class GetFoodLibraryUseCase(
    private val foodRepository: FoodRepository,
) {
    open suspend operator fun invoke(): FoodLibrary = FoodLibrary(
        custom = foodRepository.getCustomFoods(),
        history = foodRepository.getDistinctFoodsByName(),
    )
}
