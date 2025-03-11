package cz.krokviak.kalai.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.view.Surface
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CameraViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState

    private var imageCapture: ImageCapture? = null

    fun onCameraProviderReady(
        cameraProvider: ProcessCameraProvider,
        lifecycleOwner: LifecycleOwner
    ) {
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.unbindAll()

        // Preview
        val previewUseCase = Preview.Builder().build()

        // ImageCapture
        val imageCapture = ImageCapture.Builder()
            .setTargetRotation(Surface.ROTATION_0)
            .build()
        this.imageCapture = imageCapture

        try {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                previewUseCase,
                imageCapture
            )

            _uiState.value = _uiState.value.copy(previewUseCase = previewUseCase)

        } catch (exc: Exception) {
            Log.e("CameraViewModel", "Use case binding failed", exc)
        }
    }

    /**
     * Capture photo and deliver bytes through callback.
     */
    fun takePicture(onImageCaptured: (ByteArray) -> Unit) {
        val currentCapture = imageCapture ?: return

        currentCapture.takePicture(
            Dispatchers.Main.asExecutor(),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val bitmap = imageProxy.toBitmap()
                    val rotatedBitmap = rotateBitmap(bitmap, imageProxy.imageInfo.rotationDegrees.toFloat())

                    // Convert to bytes
                    val bytes = rotatedBitmap.toBytes()
                    imageProxy.close()

                    // Pass bytes back to the Activity
                    onImageCaptured(bytes)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraViewModel", "Photo capture failed: ${exception.message}", exception)
                }
            }
        )
    }

    private fun rotateBitmap(source: Bitmap, rotationDegrees: Float): Bitmap {
        if (rotationDegrees == 0f) return source
        val matrix = Matrix().apply { postRotate(rotationDegrees) }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun Bitmap.toBytes(): ByteArray {
        val outStream = java.io.ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        return outStream.toByteArray()
    }
}
