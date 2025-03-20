package cz.krokviak.kalai.detail

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalai.camera.data.FoodAnalysisDto
import cz.krokviak.kalai.common.entities.FoodItemEntity
import cz.krokviak.kalai.common.RetrofitClient
import cz.krokviak.kalai.home.MainUiState
import cz.krokviak.kalai.common.repo.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import org.threeten.bp.OffsetDateTime

class FoodDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FoodDetailState())
    val uiState: StateFlow<FoodDetailState> = _uiState

    fun loadFood(context: Context, foodId: Long) {
        val repository = FoodRepository(context)
        viewModelScope.launch(Dispatchers.IO) {
            val food = repository.getFoodItem(foodId)
            _uiState.value = FoodDetailState(
                id = food?.id ?: 0,
                name = food?.name ?: "",
                calories = food?.calories ?: 0,
                protein = food?.protein ?: 0,
                fat = food?.fat ?: 0,
                carbs = food?.carbs ?: 0,
                portion = food?.portion ?: 0,
                healthScore = food?.healthScore ?: 0,
                localImagePath = food?.localImagePath,
                createdAt = food?.createdAt ?: OffsetDateTime.now(),
                updatedAt = food?.updatedAt ?: OffsetDateTime.now()
            )
        }
    }

    fun increasePortion() {
        _uiState.update {
            it.copy(portion = it.portion + 1)
        }
    }

    fun decreasePortion() {
        if (_uiState.value.portion > 1){
            _uiState.update {
                it.copy(portion = it.portion - 1)
            }
        }
    }

    fun fixResult(context: Context) {
        val repository = FoodRepository(context)
        viewModelScope.launch(Dispatchers.IO) {
            val bytes = _uiState.value.localImagePath?.let { repository.getImageBytes(it) }
            if (bytes != null) {
                // 4) In parallel, call the backend
                val analysisJob = viewModelScope.launch(Dispatchers.IO) {
                    val analysis = analyzeImageBytes(bytes)
                    if (analysis != null) {
                        // Build updated entity
                        val updated = repository.getFoodItem(_uiState.value.id)?.copy(
                            name = analysis.title ?: "Neznámé jídlo",
                            calories = (analysis.protein * 4) + (analysis.carbs * 4) + (analysis.fat * 9),
                            protein = analysis.protein,
                            fat = analysis.fat,
                            carbs = analysis.carbs,
                            healthScore = analysis.healthScore,
                            loading = true, // STILL LOADING until animation is done
                            updatedAt = OffsetDateTime.now()
                        )
                        _uiState.update {
                            it.copy(
                                name = analysis.title ?: "Neznámé jídlo",
                                calories = (analysis.protein * 4) + (analysis.carbs * 4) + (analysis.fat * 9),
                                protein = analysis.protein,
                                fat = analysis.fat,
                                carbs = analysis.carbs,
                                healthScore = analysis.healthScore,
                            )
                        }
                    }
                }
                joinAll(analysisJob)

            }
        }
    }
    private suspend fun analyzeImageBytes(bytes: ByteArray): FoodAnalysisDto? {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = RequestBody.create(MediaType.parse("image/jpeg"), bytes)
                val response = RetrofitClient.instance.getAnalysis(requestBody).execute()
                if (response.isSuccessful) {
                    response.body()
                } else null
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to analyze image", e)
                null
            }
        }
    }

    fun finish(context: Context) {
        val repository = FoodRepository(context)
        viewModelScope.launch(Dispatchers.IO) {
            val updated = _uiState.value.copy(
                updatedAt = OffsetDateTime.now()
            )
            repository.updateFoodItem(
                FoodItemEntity(
                id = _uiState.value.id,
                name = updated.name,
                calories = updated.calories,
                protein = updated.protein,
                fat = updated.fat,
                carbs = updated.carbs,
                portion = updated.portion,
                healthScore = updated.healthScore,
                createdAt = updated.createdAt,
                updatedAt = updated.updatedAt,
                localImagePath = updated.localImagePath?: "",
                loading = false
            )
            )
        }
    }

}
