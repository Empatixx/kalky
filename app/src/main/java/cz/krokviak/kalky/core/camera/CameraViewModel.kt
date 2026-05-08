package cz.krokviak.kalky.core.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.view.Surface
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import cz.krokviak.kalky.scenes.barcode.BarcodeScanState
import cz.krokviak.kalky.core.network.OpenFoodFactsClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraViewModel(
    private val openFoodFactsClient: OpenFoodFactsClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState

    private var imageCapture: ImageCapture? = null
    private val barcodeScanner = BarcodeScanning.getClient()
    private val analysisExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var lastScannedBarcode: String? = null

    @OptIn(ExperimentalGetImage::class)
    fun onCameraProviderReady(
        cameraProvider: ProcessCameraProvider,
        lifecycleOwner: LifecycleOwner
    ) {
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.unbindAll()

        // Preview
        val previewUseCase = Preview.Builder().build()

        // ImageCapture
        val imageCaptureUseCase = ImageCapture.Builder()
            .setTargetRotation(Surface.ROTATION_0)
            .build()
        this.imageCapture = imageCaptureUseCase

        // ImageAnalysis
        val imageAnalysisUseCase = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysisUseCase.setAnalyzer(analysisExecutor, ::processImageProxy)

        try {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                previewUseCase,
                imageCaptureUseCase,
                imageAnalysisUseCase
            )

            _uiState.value = _uiState.value.copy(previewUseCase = previewUseCase)

        } catch (exc: Exception) {
            Log.e("CameraViewModel", "Use case binding failed", exc)
        }
    }

    fun setMode(mode: CameraMode) {
        if (_uiState.value.cameraMode == mode) return
        _uiState.value = _uiState.value.copy(
            cameraMode = mode,
            barcodeScanState = BarcodeScanState.Scanning
        )
        lastScannedBarcode = null
    }

    fun resetScan() {
        _uiState.value = _uiState.value.copy(barcodeScanState = BarcodeScanState.Scanning)
        lastScannedBarcode = null
    }

    fun onBarcodeDetected(barcode: String) {
        val currentState = _uiState.value
        if (currentState.cameraMode != CameraMode.QR) return
        if (currentState.barcodeScanState !is BarcodeScanState.Scanning) return
        if (barcode == lastScannedBarcode) return

        lastScannedBarcode = barcode
        _uiState.value = currentState.copy(barcodeScanState = BarcodeScanState.Loading)

        viewModelScope.launch {
            val nextState = try {
                val product = openFoodFactsClient.getProduct(barcode)
                if (product != null && product.productName != null) {
                    BarcodeScanState.ProductFound(product, barcode)
                } else {
                    BarcodeScanState.NotFound
                }
            } catch (e: Exception) {
                BarcodeScanState.Error(e.message ?: "Neznámá chyba")
            }
            _uiState.value = _uiState.value.copy(barcodeScanState = nextState)
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
                    val downscaled = downscaleBitmap(rotatedBitmap, maxEdge = 1280)

                    val bytes = downscaled.toBytes()
                    imageProxy.close()
                    if (downscaled !== rotatedBitmap) rotatedBitmap.recycle()
                    if (downscaled !== bitmap) downscaled.recycle()
                    if (rotatedBitmap !== bitmap) bitmap.recycle()

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
        val rotated = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        if (rotated !== source) source.recycle()
        return rotated
    }

    private fun downscaleBitmap(source: Bitmap, maxEdge: Int): Bitmap {
        val longest = maxOf(source.width, source.height)
        if (longest <= maxEdge) return source
        val scale = maxEdge.toFloat() / longest.toFloat()
        val targetWidth = (source.width * scale).toInt().coerceAtLeast(1)
        val targetHeight = (source.height * scale).toInt().coerceAtLeast(1)
        val scaled = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
        if (scaled !== source) source.recycle()
        return scaled
    }

    private fun Bitmap.toBytes(): ByteArray {
        val outStream = java.io.ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 85, outStream)
        return outStream.toByteArray()
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        val currentState = _uiState.value
        if (currentState.cameraMode != CameraMode.QR || currentState.barcodeScanState !is BarcodeScanState.Scanning) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue ?: continue
                    if (barcode.format == Barcode.FORMAT_EAN_13 ||
                        barcode.format == Barcode.FORMAT_EAN_8 ||
                        barcode.format == Barcode.FORMAT_UPC_A ||
                        barcode.format == Barcode.FORMAT_UPC_E
                    ) {
                        onBarcodeDetected(rawValue)
                        break
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("CameraViewModel", "Barcode scan failed", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    override fun onCleared() {
        super.onCleared()
        analysisExecutor.shutdown()
        barcodeScanner.close()
    }
}
