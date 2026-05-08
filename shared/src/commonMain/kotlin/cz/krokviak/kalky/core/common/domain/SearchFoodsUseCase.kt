package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.repo.FoodRepository

data class FoodSearchResult(
    val custom: List<FoodItemEntity>,
    val history: List<FoodItemEntity>,
)

open class SearchFoodsUseCase(
    private val foodRepository: FoodRepository,
) {
    open suspend operator fun invoke(query: String): FoodSearchResult = FoodSearchResult(
        custom = foodRepository.searchCustomFoods(query),
        history = foodRepository.searchDistinctFoodsByName(query),
    )
}

open class SearchHistoryFoodsUseCase(
    private val foodRepository: FoodRepository,
) {
    open suspend operator fun invoke(query: String): List<FoodItemEntity> =
        foodRepository.searchDistinctFoodsByName(query)
}
