package cz.krokviak.kalky.scenes.home

import cz.krokviak.kalky.core.common.FoodPhotoAnalyzer
import cz.krokviak.kalky.core.common.LiveActivityController
import cz.krokviak.kalky.core.common.domain.AddFoodItemUseCase
import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.error.UiError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

internal class PhotoCaptureController(
    private val scope: CoroutineScope,
    private val state: MutableStateFlow<MainUiState>,
    private val foodPhotoAnalyzer: FoodPhotoAnalyzer,
    private val addFoodItem: AddFoodItemUseCase,
    private val clock: Clock,
    private val liveActivityController: LiveActivityController,
    private val onAnalysisFailed: (UiError) -> Unit,
) {

    fun addFromBytes(imageBytes: ByteArray) {
        var analysisId: Long? = null
        foodPhotoAnalyzer.analyze(
            scope = scope,
            imageBytes = imageBytes,
            onPlaceholderInserted = { placeholder ->
                analysisId = placeholder.id
                liveActivityController.startFoodAnalysis(placeholder.id)
                state.update { it.copy(loadingItems = it.loadingItems.add(placeholder.id)) }
            },
            onAnalysisComplete = { item ->
                liveActivityController.completeFoodAnalysis(item.id, item.name, item.calories)
            },
            onFinalCommitted = { finalItem ->
                state.update { it.copy(loadingItems = it.loadingItems.remove(finalItem.id)) }
            },
            onAnalysisFailed = {
                analysisId?.let { liveActivityController.failFoodAnalysis(it) }
                onAnalysisFailed(UiError.PhotoAnalysis)
            }
        )
    }

    fun addFromBarcode(
        name: String,
        calories: Int,
        protein: Int,
        fat: Int,
        carbs: Int,
    ) {
        scope.launch {
            val now = clock.now()
            val item = FoodItemEntity(
                name = name,
                calories = calories,
                protein = protein,
                fat = fat,
                carbs = carbs,
                healthScore = 0,
                createdAt = now,
                updatedAt = now,
                localImagePath = "",
                loading = false,
            )
            addFoodItem(item)
        }
    }
}
