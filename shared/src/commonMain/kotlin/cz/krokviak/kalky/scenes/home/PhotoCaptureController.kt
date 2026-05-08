package cz.krokviak.kalky.scenes.home

import cz.krokviak.kalky.core.common.FoodPhotoAnalyzer
import cz.krokviak.kalky.core.common.domain.AddFoodItemUseCase
import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.common.error.UiError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Orchestrates the "add food item" pipelines (camera photo + barcode).
 *
 * Since H10, the SQLDelight reactive flow in MainViewModel reflects DB writes
 * automatically — placeholder, analyzed, and final-committed states each
 * trigger a re-emit. This controller therefore only manages the [loadingItems]
 * UI marker and the analysis-failed signal; the food list itself is owned by
 * the flow.
 */
internal class PhotoCaptureController(
    private val scope: CoroutineScope,
    private val state: MutableStateFlow<MainUiState>,
    private val foodPhotoAnalyzer: FoodPhotoAnalyzer,
    private val addFoodItem: AddFoodItemUseCase,
    private val clock: Clock,
    private val onAnalysisFailed: (UiError) -> Unit,
) {

    fun addFromBytes(imageBytes: ByteArray) {
        foodPhotoAnalyzer.analyze(
            scope = scope,
            imageBytes = imageBytes,
            onPlaceholderInserted = { placeholder ->
                state.update { it.copy(loadingItems = it.loadingItems.add(placeholder.id)) }
            },
            onAnalysisComplete = { /* DB flow re-emits; no-op */ },
            onFinalCommitted = { finalItem ->
                state.update { it.copy(loadingItems = it.loadingItems.remove(finalItem.id)) }
            },
            onAnalysisFailed = { onAnalysisFailed(UiError.PhotoAnalysis) }
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
            // The daily-macros flow picks up the new row and updates state.
        }
    }
}
