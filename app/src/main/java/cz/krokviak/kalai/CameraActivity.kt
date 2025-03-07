package cz.krokviak.kalai

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    setMainContent()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
                }
            }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setMainContent()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun setMainContent() {
        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        // Keep track of whether we have a captured image
        var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

        // If we have no captured image, show camera;
        // otherwise, show the preview screen.
        if (capturedImageUri == null) {
            CameraScreen(
                onImageCaptured = { file ->
                    capturedImageUri = Uri.fromFile(file)
                }
            )
        } else {
            PreviewScreen(
                imageUri = capturedImageUri!!,
                onDismiss = {
                    // When user dismisses or you want to reset,
                    // reset the URI to go back to the camera screen
                    capturedImageUri = null
                }
            )
        }
    }

    @Composable
    fun CameraScreen(
        onImageCaptured: (File) -> Unit
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        // Reference to the CameraX preview
        var previewView by remember { mutableStateOf<PreviewView?>(null) }
        // We'll store ImageCapture in state
        val imageCaptureRef = remember { mutableStateOf<ImageCapture?>(null) }

        // Set up the camera when this composable first appears
        LaunchedEffect(Unit) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val previewUseCase = Preview.Builder().build().also { preview ->
                    preview.setSurfaceProvider(previewView?.surfaceProvider)
                }

                val imageCapture = ImageCapture.Builder().build()
                imageCaptureRef.value = imageCapture

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        previewUseCase,
                        imageCapture
                    )
                } catch (exc: Exception) {
                    Log.e("CameraX", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(context))
        }

        // UI layout
        Box(modifier = Modifier.fillMaxSize()) {
            // Camera preview
            AndroidView(
                factory = { ctx -> PreviewView(ctx).also { previewView = it } },
                modifier = Modifier.fillMaxSize()
            )

            // Capture button at bottom center
            CaptureButton(
                onClick = {
                    val imageCapture = imageCaptureRef.value ?: return@CaptureButton
                    val file = File(
                        context.externalMediaDirs.firstOrNull(),
                        "${System.currentTimeMillis()}.jpg"
                    )

                    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                    imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                onImageCaptured(file)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
                            }
                        }
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }

    /**
     * A simple composable for the capture button.
     * You can reuse your existing Canvas-based button or any other UI.
     */
    @Composable
    fun CaptureButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Canvas(
            modifier = modifier
                .size(80.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick
                )
        ) {
            // Outer ring
            drawCircle(
                color = Color.White,
                style = Stroke(width = 4.dp.toPx())
            )
            // Inner circle
            drawCircle(
                color = Color.White,
                radius = size.minDimension / 2.5f
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PreviewScreen(
        imageUri: Uri,
        onDismiss: () -> Unit
    ) {
        // You could show the background image behind everything using a Box,
        // or do something else. Here, we show the image in the "background",
        // then a sheet slides from the bottom.
        Box(modifier = Modifier.fillMaxSize()) {
            // Using Coil’s AsyncImage for example:
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                // e.g. contentScale = ContentScale.Crop
            )

            // Now the bottom sheet on top.
            // This triggers a standard modal bottom sheet that dims the background.
            ModalBottomSheet(
                onDismissRequest = onDismiss
            ) {
                // Bottom sheet content — layout as you wish
                Text(
                    text = "Captured Image: $imageUri",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                // Add more controls here, e.g. buttons to confirm the photo, retake, share, etc.
            }
        }
    }

}