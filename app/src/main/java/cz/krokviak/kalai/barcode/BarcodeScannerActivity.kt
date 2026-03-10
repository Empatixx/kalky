package cz.krokviak.kalai.barcode

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import cz.krokviak.kalai.theme.KalaiTheme
import java.util.concurrent.Executors

class BarcodeScannerActivity : AppCompatActivity() {

    private val viewModel: BarcodeScannerViewModel by viewModel()
    private var previewView: PreviewView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startCamera()
                } else {
                    Toast.makeText(this, "Přístup ke kameře zamítnut", Toast.LENGTH_LONG).show()
                    finish()
                }
            }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindCameraAndSetContent(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun bindCameraAndSetContent(cameraProvider: ProcessCameraProvider) {
        cameraProvider.unbindAll()

        val preview = Preview.Builder().build()
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val barcodeScanner = BarcodeScanning.getClient()
        val analysisExecutor = Executors.newSingleThreadExecutor()

        imageAnalysis.setAnalyzer(analysisExecutor) { imageProxy ->
            processImageProxy(barcodeScanner, imageProxy)
        }

        try {
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            Log.e("BarcodeScanner", "Camera binding failed", e)
        }

        setContent {
            KalaiTheme {
                val state by viewModel.state.collectAsState()

                BarcodeScannerScreen(
                    state = state,
                    cameraPreview = {
                        AndroidView(
                            factory = { context ->
                                PreviewView(context).also { pv ->
                                    previewView = pv
                                    preview.surfaceProvider = pv.surfaceProvider
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    },
                    onAddClick = { product, quantity ->
                        val nutriments = product.nutriments
                        val multiplier = quantity / 100.0
                        val intent = Intent().apply {
                            putExtra("name", product.productName ?: "Neznámý produkt")
                            putExtra("calories", ((nutriments?.energyKcal100g ?: 0.0) * multiplier).toInt())
                            putExtra("protein", ((nutriments?.proteins100g ?: 0.0) * multiplier).toInt())
                            putExtra("fat", ((nutriments?.fat100g ?: 0.0) * multiplier).toInt())
                            putExtra("carbs", ((nutriments?.carbohydrates100g ?: 0.0) * multiplier).toInt())
                            putExtra("imageUrl", product.imageFrontUrl ?: "")
                        }
                        setResult(RESULT_OK, intent)
                        finish()
                    },
                    onRetryClick = { viewModel.resetScan() },
                    onCloseClick = { finish() }
                )
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(
        barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
        imageProxy: ImageProxy
    ) {
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
                        viewModel.onBarcodeDetected(rawValue)
                        break
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("BarcodeScanner", "Barcode scan failed", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}
