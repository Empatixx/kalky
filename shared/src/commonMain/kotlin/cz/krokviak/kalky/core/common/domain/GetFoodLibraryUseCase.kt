package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.repo.FoodRepository

data class FoodLibrary(
    val custom: List<FoodItemEntity>,
    val history: List<FoodItemEntity>,
)

/**
 * Reads the static food library (custom-defined foods + distinct items by name from history).
 * Used by CustomFoodSearchViewModel for the initial load and after a query is cleared.
 */
open class GetFoodLibraryUseCase(
    private val foodRepository: FoodRepository,
) {
    open suspend operator fun invoke(): FoodLibrary = FoodLibrary(
        custom = foodRepository.getCustomFoods(),
        history = foodRepository.getDistinctFoodsByName(),
    )
}
