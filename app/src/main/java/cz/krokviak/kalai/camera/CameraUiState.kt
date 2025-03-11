package cz.krokviak.kalai.camera

import android.graphics.Bitmap
import androidx.camera.core.Preview
import cz.krokviak.kalai.camera.data.FoodAnalysisDto

data class CameraUiState(
    val previewUseCase: Preview? = null,
)