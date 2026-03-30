package cz.krokviak.kalky.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.krokviak.kalky.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalky.i18n.CzechStrings
import cz.krokviak.kalky.i18n.EnglishStrings
import cz.krokviak.kalky.settings.AppLanguage
import cz.krokviak.kalky.settings.AppPreferencesManager
import cz.krokviak.kalky.theme.KalkyTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CameraActivity : AppCompatActivity() {

    private val cameraViewModel: CameraViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ask for camera permission
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    initCameraAndSetContent()
                } else {
                    val strings = if (AppPreferencesManager.language.value == AppLanguage.EN) EnglishStrings else CzechStrings
                    Toast.makeText(this, strings.camera.permissionDenied, Toast.LENGTH_LONG).show()
                    finish()
                }
            }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            initCameraAndSetContent()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun initCameraAndSetContent() {
        // Initialize camera provider
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            // Pass the Activity (LifecycleOwner) to your ViewModel
            cameraViewModel.onCameraProviderReady(cameraProvider, this)
        }, ContextCompat.getMainExecutor(this))

        // Set Compose content
        setContent {
            KalkyTheme {
                val uiState = cameraViewModel.uiState.collectAsStateWithLifecycle()
                CameraScreen(
                    cameraViewModel = cameraViewModel,
                    uiState = uiState.value,
                    onPictureBytesReady = { bytes ->
                        val tempImageFile = File.createTempFile("cameraResult", ".png", cacheDir)
                        FileOutputStream(tempImageFile).use { fos ->
                            fos.write(bytes)
                        }
                        setResult(
                            RESULT_OK,
                            Intent().apply {
                                putExtra(EXTRA_RESULT_TYPE, RESULT_TYPE_PHOTO)
                                putExtra(EXTRA_IMAGE_URL, tempImageFile.absolutePath)
                            }
                        )
                        finish()
                    },
                    onAddBarcodeClick = { product, quantity ->
                        finishWithBarcodeResult(product, quantity)
                    },
                    onCloseClick = { finish() }
                )
            }
        }
    }

    private fun finishWithBarcodeResult(product: OpenFoodFactsProduct, quantity: Int) {
        val nutriments = product.nutriments
        val multiplier = quantity / 100.0

        setResult(
            RESULT_OK,
            Intent().apply {
                putExtra(EXTRA_RESULT_TYPE, RESULT_TYPE_BARCODE)
                val strings = if (AppPreferencesManager.language.value == AppLanguage.EN) EnglishStrings else CzechStrings
                putExtra(EXTRA_NAME, product.productName ?: strings.common.unknownProduct)
                putExtra(EXTRA_CALORIES, ((nutriments?.energyKcal100g ?: 0.0) * multiplier).toInt())
                putExtra(EXTRA_PROTEIN, ((nutriments?.proteins100g ?: 0.0) * multiplier).toInt())
                putExtra(EXTRA_FAT, ((nutriments?.fat100g ?: 0.0) * multiplier).toInt())
                putExtra(EXTRA_CARBS, ((nutriments?.carbohydrates100g ?: 0.0) * multiplier).toInt())
                putExtra(EXTRA_IMAGE_URL, product.imageFrontUrl ?: "")
            }
        )
        finish()
    }

    companion object {
        const val EXTRA_RESULT_TYPE = "resultType"
        const val EXTRA_IMAGE_URL = "imageUrl"
        const val EXTRA_NAME = "name"
        const val EXTRA_CALORIES = "calories"
        const val EXTRA_PROTEIN = "protein"
        const val EXTRA_FAT = "fat"
        const val EXTRA_CARBS = "carbs"

        const val RESULT_TYPE_PHOTO = "photo"
        const val RESULT_TYPE_BARCODE = "barcode"
    }
}
