package cz.krokviak.kalai.camera

import android.graphics.Bitmap
import androidx.camera.core.Preview
import cz.krokviak.kalai.camera.data.FoodAnalysisDto

data class CameraUiState(
    val cameraScreenState: CameraScreenState = CameraScreenState.PREVIEW,
    val previewUseCase: Preview? = null,
    val capturedBitmap: Bitmap? = null,
    val portion: Int = 1,
    val foodAnalysisData: FoodAnalysisDto? = null,
    val analyzing: Boolean = false
)

enum class CameraScreenState {
    PREVIEW,
    CAPTURED
}
