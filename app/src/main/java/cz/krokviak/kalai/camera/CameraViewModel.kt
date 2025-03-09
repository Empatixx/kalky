// CameraViewModel.kt
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
import cz.krokviak.kalai.common.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

class CameraViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState

    // Keep references to ImageCapture
    private var imageCapture: ImageCapture? = null

    fun onCameraProviderReady(
        cameraProvider: ProcessCameraProvider,
        lifecycleOwner: LifecycleOwner
    ) {
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider.unbindAll()

        // Build a Preview use case
        val previewUseCase = Preview.Builder().build()

        // Build an ImageCapture use case
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

    fun takePicture() {
        val currentCapture = imageCapture ?: return
        currentCapture.takePicture(
            Dispatchers.Main.asExecutor(),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val bitmap = imageProxy.toBitmap()
                    val rotatedBitmap =
                        rotateBitmap(bitmap, imageProxy.imageInfo.rotationDegrees.toFloat())
                    imageProxy.close()
                    _uiState.value = _uiState.value.copy(
                        capturedBitmap = rotatedBitmap,
                        analyzing = true,
                        cameraScreenState = CameraScreenState.CAPTURED
                    )
                    analyzeImage()

                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraViewModel", "Photo capture failed: ${exception.message}", exception)
                }
            }
        )
    }

    fun increasePortion() {
        _uiState.value = _uiState.value.copy(portion = _uiState.value.portion + 1)
    }

    fun decreasePortion() {
        val current = _uiState.value.portion
        if (current > 1) {
            _uiState.value = _uiState.value.copy(portion = current - 1)
        }
    }

    fun analyzeImage() {
        val currentBitmap = _uiState.value.capturedBitmap ?: return

        // Set analyzing to true
        _uiState.value = _uiState.value.copy(analyzing = true)

        val stream = ByteArrayOutputStream()
        currentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val bytes = stream.toByteArray()
        val requestBody = RequestBody.create(MediaType.parse("image/jpeg"), bytes)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getAnalysis(requestBody).execute()
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        _uiState.value = _uiState.value.copy(
                            foodAnalysisData = data
                        )
                    }
                } else {
                    Log.e("CameraViewModel", "Response not successful: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CameraViewModel", "Error calling analysis API", e)
            } finally {
                // Analysis is complete or failed
                _uiState.value = _uiState.value.copy(analyzing = false)
            }
        }
    }

    /**
     * Utility to rotate a bitmap by certain degrees.
     */
    private fun rotateBitmap(source: Bitmap, rotationDegrees: Float): Bitmap {
        if (rotationDegrees == 0f) return source
        val matrix = Matrix().apply { postRotate(rotationDegrees) }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}
