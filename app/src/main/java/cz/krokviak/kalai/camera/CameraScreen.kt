// CameraScreen.kt
package cz.krokviak.kalai.camera

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.krokviak.kalai.camera.components.CapturedContentUI
import cz.krokviak.kalai.camera.components.CameraPreviewUI
import java.io.ByteArrayOutputStream

@Composable
fun CameraScreen(
    cameraViewModel: CameraViewModel,
    uiState: CameraUiState,
    onPictureBytesReady: (ByteArray) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreviewUI(
            previewUseCase = uiState.previewUseCase,
            onSurfaceProviderCreated = { previewView ->
                uiState.previewUseCase?.setSurfaceProvider(previewView.surfaceProvider)
            },
            onCaptureClick = {
                cameraViewModel.takePicture { bytes ->
                    onPictureBytesReady(bytes)
                }
            }
        )
    }
}