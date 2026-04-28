package cz.krokviak.kalky.common.domain

import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.common.repo.FoodRepository

data class FoodLibrary(
    val custom: List<FoodItemEntity>,
    val history: List<FoodItemEntity>,
)

/**
 * Reads the static food library (custom-defined foods + distinct items by name from history).
 * Used by CustomFoodSearchViewModel for the initial load and after a query is cleared.
 */
class GetFoodLibraryUseCase(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(): FoodLibrary = FoodLibrary(
        custom = foodRepository.getCustomFoods(),
        history = foodRepository.getDistinctFoodsByName(),
    )
}
