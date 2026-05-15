package cz.krokviak.kalky.core.common

import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.repo.FoodRepository
import cz.krokviak.kalky.core.common.utils.caloriesFromMacros
import cz.krokviak.kalky.core.network.FoodAnalysisClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

private const val LOADING_ANIMATION_DURATION_MS = 6000L

open class FoodPhotoAnalyzer(
    private val foodRepository: FoodRepository,
    private val foodAnalysisClient: FoodAnalysisClient,
    private val imageStorage: ImageStorage,
    private val clock: Clock,
) {

    open fun analyze(
        scope: CoroutineScope,
        imageBytes: ByteArray,
        onPlaceholderInserted: (FoodItemEntity) -> Unit,
        onAnalysisComplete: (FoodItemEntity) -> Unit,
        onFinalCommitted: (FoodItemEntity) -> Unit,
        onAnalysisFailed: () -> Unit = {},
    ): Job = scope.launch {
        val imagePath = imageStorage.storeImageFile(imageBytes)

        val now = clock.now()
        val placeholder = FoodItemEntity(
            createdAt = now,
            updatedAt = now,
            localImagePath = imagePath,
            loading = true
        )

        val newId = foodRepository.insertFoodItem(placeholder)
        val insertedItem = placeholder.copy(id = newId)
        onPlaceholderInserted(insertedItem)

        val animationJob = launch { delay(LOADING_ANIMATION_DURATION_MS) }
        val analysisJob = launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { foodAnalysisClient.getAnalysis(imageBytes) }
            }
            val analysis = result.getOrNull()
            if (analysis != null) {
                val updated = insertedItem.copy(
                    name = analysis.title ?: insertedItem.name,
                    calories = caloriesFromMacros(analysis.protein, analysis.carbs, analysis.fat),
                    protein = analysis.protein,
                    fat = analysis.fat,
                    carbs = analysis.carbs,
                    healthScore = analysis.healthScore,
                    loading = true,
                    updatedAt = clock.now()
                )
                foodRepository.updateFoodItem(updated)
                onAnalysisComplete(updated)
            } else {
                onAnalysisFailed()
            }
        }

        joinAll(animationJob, analysisJob)

        val analyzed = foodRepository.getFoodItem(newId) ?: insertedItem
        val finalItem = analyzed.copy(loading = false, updatedAt = clock.now())
        foodRepository.updateFoodItem(finalItem)
        onFinalCommitted(finalItem)
    }
}
