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
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import cz.krokviak.kalai.common.entities.FoodItemEntity
import cz.krokviak.kalai.common.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import java.io.File
import java.io.FileOutputStream
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
        // Initialize camera provider
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            // Pass the Activity (LifecycleOwner) to your ViewModel
            cameraViewModel.onCameraProviderReady(cameraProvider, this)
        }, ContextCompat.getMainExecutor(this))

        // Set Compose content
        setContent {
            val uiState = cameraViewModel.uiState.collectAsStateWithLifecycle()
            CameraScreen(
                cameraViewModel = cameraViewModel,
                uiState = uiState.value,
                onPictureBytesReady = { bytes ->
                    val tempImageFile = File.createTempFile("cameraResult", ".png", cacheDir)
                    FileOutputStream(tempImageFile).use { fos ->
                        fos.write(bytes)
                    }
                    val intent = Intent().apply {
                        putExtra("imageUrl", tempImageFile.absolutePath)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            )
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
