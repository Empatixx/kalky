package cz.krokviak.kalai.camera

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.krokviak.kalai.camera.components.CapturedContentUI
import cz.krokviak.kalai.camera.components.CameraPreviewUI
import java.io.ByteArrayOutputStream

/**
 * Main composable that decides whether to show the camera preview
 * or the captured content based on the [uiState].
 */
@Composable
fun CameraScreen(
    cameraViewModel: CameraViewModel,
    uiState: CameraUiState,
    onConfirm: (portion: Int, imageBytes: ByteArray) -> Unit
) {
    val screenState = uiState.cameraScreenState

    Box(modifier = Modifier.fillMaxSize()) {
        when (screenState) {
            CameraScreenState.PREVIEW -> {
                CameraPreviewUI(
                    previewUseCase = uiState.previewUseCase,
                    onSurfaceProviderCreated = { previewView ->
                        // Connect the preview use case to the surface provider
                        uiState.previewUseCase?.setSurfaceProvider(previewView.surfaceProvider)
                    },
                    onCaptureClick = { cameraViewModel.takePicture() }
                )
            }
            CameraScreenState.CAPTURED -> {
                uiState.capturedBitmap?.let { bmp ->
                    CapturedContentUI(
                        bitmap = bmp,
                        analysisData = uiState.foodAnalysisData,
                        portion = uiState.portion,
                        onIncreasePortion = { cameraViewModel.increasePortion() },
                        onDecreasePortion = { cameraViewModel.decreasePortion() },
                        onFixResults = { cameraViewModel.analyzeImage() },
                        onConfirm = {
                            // Return it to the Activity
                            val stream = ByteArrayOutputStream()
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            val bytes = stream.toByteArray()
                            onConfirm(uiState.portion, bytes)
                        }
                    )
                }
            }
        }
    }
}
