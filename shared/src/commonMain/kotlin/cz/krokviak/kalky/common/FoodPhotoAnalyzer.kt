package cz.krokviak.kalky.common

import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.common.repo.FoodRepository
import cz.krokviak.kalky.network.FoodAnalysisClient
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

/**
 * Orchestrates the "take photo → show loading → analyze → commit" pipeline.
 *
 * Caller provides:
 *  - [onPlaceholderInserted]: called immediately after DB insert of the placeholder item
 *    so UI can add it to its list.
 *  - [onAnalysisComplete]: called after analysis arrives (loading=true still). Lets UI
 *    render the analyzed macros while the 6s loading animation keeps running.
 *  - [onFinalCommitted]: called once both the 6s animation window and analysis are done;
 *    the item has loading=false and is ready for final UI state.
 *
 * The animation window (6 s) runs in parallel with the network call; both must complete
 * before the final commit fires. If analysis returns null, the placeholder is committed
 * as-is with loading=false.
 */
class FoodPhotoAnalyzer(
    private val foodRepository: FoodRepository,
    private val foodAnalysisClient: FoodAnalysisClient,
    private val imageStorage: ImageStorage,
) {

    fun analyze(
        scope: CoroutineScope,
        imageBytes: ByteArray,
        onPlaceholderInserted: (FoodItemEntity) -> Unit,
        onAnalysisComplete: (FoodItemEntity) -> Unit,
        onFinalCommitted: (FoodItemEntity) -> Unit,
        onAnalysisFailed: () -> Unit = {},
    ): Job = scope.launch {
        val imagePath = imageStorage.storeImageFile(imageBytes)

        val now = Clock.System.now()
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
                    calories = (analysis.protein * 4) + (analysis.carbs * 4) + (analysis.fat * 9),
                    protein = analysis.protein,
                    fat = analysis.fat,
                    carbs = analysis.carbs,
                    healthScore = analysis.healthScore,
                    loading = true,
                    updatedAt = Clock.System.now()
                )
                foodRepository.updateFoodItem(updated)
                onAnalysisComplete(updated)
            } else {
                onAnalysisFailed()
            }
        }

        joinAll(animationJob, analysisJob)

        val analyzed = foodRepository.getFoodItem(newId) ?: insertedItem
        val finalItem = analyzed.copy(loading = false, updatedAt = Clock.System.now())
        foodRepository.updateFoodItem(finalItem)
        onFinalCommitted(finalItem)
    }
}
