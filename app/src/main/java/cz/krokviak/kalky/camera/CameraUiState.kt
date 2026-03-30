package cz.krokviak.kalky.camera

import androidx.camera.core.Preview
import cz.krokviak.kalky.barcode.BarcodeScanState

enum class CameraMode {
    PHOTO,
    QR
}

data class CameraUiState(
    val previewUseCase: Preview? = null,
    val cameraMode: CameraMode = CameraMode.PHOTO,
    val barcodeScanState: BarcodeScanState = BarcodeScanState.Scanning
)
