package cz.krokviak.kalai.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import cz.krokviak.kalai.camera.entities.FoodItemEntity
import cz.krokviak.kalai.common.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime
import java.io.File
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraViewModel: CameraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraViewModel = ViewModelProvider(this)[CameraViewModel::class.java]

        // Ask for camera permission
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    initCameraAndSetContent()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
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
        // Initialize camera provider once
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            // Pass the Activity (which is a LifecycleOwner) to your ViewModel
            cameraViewModel.onCameraProviderReady(cameraProvider, this)
        }, ContextCompat.getMainExecutor(this))

        // Set Compose content
        setContent {
            MaterialTheme {
                val uiState by cameraViewModel.uiState.collectAsStateWithLifecycle()
                CameraScreen(
                    cameraViewModel = cameraViewModel,
                    uiState = uiState,
                    onConfirm = { portion, bytes ->
                        lifecycleScope.launch {
                            val imageFile = storeImage(bytes)
                            val foodItem = withContext(Dispatchers.IO) {
                                createEntity(uiState, imageFile.absolutePath, portion)
                            }
                            val intent = Intent().apply {
                                putExtra("foodEntity", foodItem)
                            }
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    }
                )
            }
        }
    }
    private fun createEntity(uiState: CameraUiState, path: String, portion: Int): FoodItemEntity{
        val foodItem = FoodItemEntity(
            name = uiState.foodAnalysisData?.title ?: "Unknown",
            calories = uiState.foodAnalysisData?.calories ?: 0,
            protein = uiState.foodAnalysisData?.protein ?: 0,
            fat = uiState.foodAnalysisData?.fat ?: 0,
            carbs = uiState.foodAnalysisData?.carbs ?: 0,
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now(),
            portion = portion,
            localImagePath = path
        )
        val id = DatabaseProvider.instance.foodItemDao().insertFoodItem(foodItem)
        return foodItem.copy(id = id)
    }

    private fun storeImage(bytes: ByteArray): File {
        val photoUuid = UUID.randomUUID().toString()
        val photosDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(photosDir, "photo_${photoUuid}.jpg")
        imageFile.writeBytes(bytes)
        return imageFile
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
