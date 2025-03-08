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
import cz.krokviak.kalai.camera.clients.RetrofitClient
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

    fun onCameraProviderReady(    cameraProvider: ProcessCameraProvider,
                                  lifecycleOwner: LifecycleOwner
    ) {
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        // Unbind any previous use cases
        cameraProvider.unbindAll()

        // Build a Preview use case
        val previewUseCase = Preview.Builder().build().also { preview ->
            // We do not set the surface provider here, will be assigned in the UI
        }

        // Build an ImageCapture use case
        val imageCapture = ImageCapture.Builder()
            .setTargetRotation(Surface.ROTATION_0)
            .build()
        this.imageCapture = imageCapture

        try {
            // Bind our use cases to the lifecycle of whoever uses it (Activity or composition)
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                previewUseCase,
                imageCapture
            )

            // Update the UI state with the previewUseCase to set its surface provider
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
                    val rotatedBitmap = rotateBitmap(bitmap, imageProxy.imageInfo.rotationDegrees.toFloat())
                    imageProxy.close()

                    _uiState.value = _uiState.value.copy(
                        capturedBitmap = rotatedBitmap,
                        cameraScreenState = CameraScreenState.CAPTURED
                    )
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
        // Convert to ByteArray
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
                        // Update UI state on main thread
                        _uiState.value = _uiState.value.copy(
                            foodAnalysisData = data
                        )
                    }
                } else {
                    Log.e("CameraViewModel", "Response not successful: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CameraViewModel", "Error calling analysis API", e)
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