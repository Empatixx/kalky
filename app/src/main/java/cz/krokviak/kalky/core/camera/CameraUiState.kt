package cz.krokviak.kalky.core.camera

import androidx.camera.core.Preview
import cz.krokviak.kalky.scenes.barcode.BarcodeScanState

enum class CameraMode {
    PHOTO,
    QR
}

data class CameraUiState(
    val previewUseCase: Preview? = null,
    val cameraMode: CameraMode = CameraMode.PHOTO,
    val barcodeScanState: BarcodeScanState = BarcodeScanState.Scanning
)
