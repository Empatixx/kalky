package cz.krokviak.kalky.home

import cz.krokviak.kalky.common.FoodPhotoAnalyzer
import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.common.error.UiError
import cz.krokviak.kalky.common.repo.FoodRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Orchestrates the "add food item" pipelines (camera photo + barcode) and keeps
 * MainViewModel free of analysis bookkeeping.
 *
 * Owns no state of its own — mutates the [state] flow passed in by the caller
 * and reports the analysis-failed branch back through [onAnalysisFailed] so the
 * owner can map it to whatever UiError representation it uses.
 */
internal class PhotoCaptureController(
    private val scope: CoroutineScope,
    private val state: MutableStateFlow<MainUiState>,
    private val foodRepository: FoodRepository,
    private val foodPhotoAnalyzer: FoodPhotoAnalyzer,
    private val clock: Clock,
    private val onMacrosChanged: () -> Unit,
    private val onAnalysisFailed: (UiError) -> Unit,
) {

    fun addFromBytes(imageBytes: ByteArray) {
        foodPhotoAnalyzer.analyze(
            scope = scope,
            imageBytes = imageBytes,
            onPlaceholderInserted = { placeholder ->
                state.update { current ->
                    current.copy(
                        recentlyAddedItems = current.recentlyAddedItems.mutate { it.add(0, placeholder) },
                        loadingItems = current.loadingItems.add(placeholder.id),
                    )
                }
                onMacrosChanged()
            },
            onAnalysisComplete = { analyzed ->
                state.update { current ->
                    current.copy(
                        recentlyAddedItems = current.recentlyAddedItems.replaceById(analyzed.id, analyzed),
                    )
                }
            },
            onFinalCommitted = { finalItem ->
                state.update { current ->
                    current.copy(
                        loadingItems = current.loadingItems.remove(finalItem.id),
                        recentlyAddedItems = current.recentlyAddedItems.replaceById(finalItem.id, finalItem),
                    )
                }
                onMacrosChanged()
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
            val newId = foodRepository.insertFoodItem(item)
            val insertedItem = item.copy(id = newId)
            state.update { current ->
                current.copy(
                    recentlyAddedItems = current.recentlyAddedItems.mutate { it.add(0, insertedItem) },
                )
            }
            onMacrosChanged()
        }
    }
}

internal fun PersistentList<FoodItemEntity>.replaceById(
    id: Long,
    replacement: FoodItemEntity,
): PersistentList<FoodItemEntity> {
    val idx = indexOfFirst { it.id == id }
    return if (idx < 0) this else mutate { it[idx] = replacement }
}
