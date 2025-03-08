package cz.krokviak.kalai

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        // Ask for camera permission
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    setMainContent()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
                    finish()
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

    private fun setMainContent() {
        setContent {
            MaterialTheme {
                CameraScreen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

/**
 * Main screen that toggles between preview and captured state.
 */
@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var screenState by remember { mutableStateOf(CameraScreenState.PREVIEW) }

    // CameraX references
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    val imageCaptureRef = remember { mutableStateOf<ImageCapture?>(null) }

    // Store the captured in-memory bitmap
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var portion by remember { mutableStateOf(1) }

    // Set up camera once
    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview use case
            val previewUseCase = Preview.Builder().build().also { preview ->
                preview.setSurfaceProvider(previewView?.surfaceProvider)
            }

            // ImageCapture use case
            val imageCapture = ImageCapture.Builder()
                .setTargetRotation(previewView?.display?.rotation ?: Surface.ROTATION_0)
                .build()
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

    Box(modifier = Modifier.fillMaxSize()) {
        when (screenState) {
            CameraScreenState.PREVIEW -> {
                // Show the camera preview UI
                CameraPreview(
                    previewView = { newView -> previewView = newView },
                    onCapture = { bitmap ->
                        // Once captured, store it and switch state
                        capturedBitmap = bitmap
                        screenState = CameraScreenState.CAPTURED
                    },
                    imageCapture = imageCaptureRef.value
                )
            }

            CameraScreenState.CAPTURED -> {
                // Show the captured photo UI
                capturedBitmap?.let { bmp ->
                    CapturedContent(
                        bitmap = bmp,
                        onConfirm = {
                            // Compress bitmap to ByteArray
                            val stream = ByteArrayOutputStream()
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            val bytes = stream.toByteArray()

                            // Return it via Intent
                            val intent = Intent().apply {
                                putExtra("capturedImage", bytes)
                                // -- NEW: also put the portion count --
                                putExtra("amount", portion)
                            }
                            (context as Activity).setResult(Activity.RESULT_OK, intent)
                            (context as Activity).finish()
                        },
                        onFixResults = {

                        },
                        portion = portion,
                        onIncreasePortion = {
                            portion += 1
                        },
                        onDecreasePortion = {
                            if (portion > 1) portion -= 1
                        }
                    )
                }
            }
        }
    }
}

/**
 * Composable that shows the camera preview and a capture button.
 */
@Composable
fun CameraPreview(
    previewView: (PreviewView) -> Unit,
    onCapture: (Bitmap) -> Unit,
    imageCapture: ImageCapture?
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Camera feed
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also { previewView(it) }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Capture button
        val context = LocalContext.current
        CaptureButton(
            onClick = {
                val currentImageCapture = imageCapture ?: return@CaptureButton
                currentImageCapture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(imageProxy: ImageProxy) {
                            val rawBitmap = imageProxy.toBitmap()
                            val rotation = imageProxy.imageInfo.rotationDegrees.toFloat()
                            val rotatedBitmap = rotateBitmap(rawBitmap, rotation)
                            imageProxy.close()

                            // Pass the final bitmap back
                            onCapture(rotatedBitmap)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e(
                                "CameraX",
                                "Photo capture failed: ${exception.message}",
                                exception
                            )
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
 * Shows the captured image and a bottom-sheet-like card (not draggable).
 */
@Composable
fun CapturedContent(
    bitmap: Bitmap,
    onConfirm: () -> Unit,
    onFixResults: () -> Unit,
    portion: Int,
    onIncreasePortion: () -> Unit,
    onDecreasePortion: () -> Unit
) {
    // The height of your bottom sheet
    val bottomSheetHeight = 450.dp

    // We shift the image upward by half of the bottom sheet height
    val offset = bottomSheetHeight / 2

    Box(modifier = Modifier.fillMaxSize()) {
        // Offset the image so that 50% of it remains behind the card
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .offset(y = -offset),
            contentScale = ContentScale.Crop
        )

        // Bottom sheet card stays the same
        BottomSheetCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(bottomSheetHeight),
            onConfirm = onConfirm,
            onFixResults = onFixResults,
            portion = portion,
            onIncreasePortion = onIncreasePortion,
            onDecreasePortion = onDecreasePortion
        )
    }
}


/**
 * Bottom sheet UI with the requested design elements.
 */
@Composable
fun BottomSheetCard(
    modifier: Modifier,
    onConfirm: () -> Unit,
    onFixResults: () -> Unit,
    portion: Int,
    onIncreasePortion: () -> Unit,
    onDecreasePortion: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 16.dp, 16.dp, 16.dp)
        ) {
            FoodTypeBadge(
                type = "Breakfast",
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            TitleAndAmountRow(
                title = "Pancakes with blueberries & syrup",
                amount = portion,
                onIncrease = onIncreasePortion,
                onDecrease = onDecreasePortion
            )

            Spacer(modifier = Modifier.height(16.dp))

            NutrientsGrid(
                caloriesValue = "350",
                carbsValue = "45g",
                proteinValue = "12g",
                fatsValue = "10g"
            )

            Spacer(modifier = Modifier.height(16.dp))

            HealthQualityBar(
                score = 7,
                maxScore = 10
            )

            Spacer(modifier = Modifier.height(16.dp))
            ButtonsRow(
                onFixResults = onFixResults,
                onConfirm = onConfirm)

        }
    }
}

@Composable
fun TitleAndAmountRow(
    title: String,
    amount: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(0.65f)
                .padding(start = 8.dp, end = 8.dp)
        )

        OutlinedCard(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(0.35f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDecrease) {
                    Icon(
                        imageVector = Icons.Outlined.Remove,
                        contentDescription = "Decrease",
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text("$amount")
                IconButton(onClick = onIncrease) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Increase",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun FoodTypeBadge(
    type: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(4.dp)
    ) {
        Text(
            text = type,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun ButtonsRow(
    onFixResults: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
            onClick = onFixResults,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(50),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
                containerColor = MaterialTheme.colorScheme.surface,
            )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.iconai),
                modifier = Modifier.size(24.dp).padding(end = 8.dp),
                contentDescription = null
            )
            Text("Fix results")
        }

        Button(
            onClick = onConfirm,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Text("Done")
        }
    }
}


@Composable
fun NutrientsGrid(
    caloriesValue: String = "350",
    carbsValue: String = "45g",
    proteinValue: String = "12g",
    fatsValue: String = "10g"
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PhotoNutrientCard("Calories", caloriesValue, modifier = Modifier.weight(1f), icon = Icons.Outlined.LocalFireDepartment)
            PhotoNutrientCard("Carbs", carbsValue, modifier = Modifier.weight(1f), icon = ImageVector.vectorResource(R.drawable.wheat), iconTintColor = colorResource(id = R.color.carbsColor))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PhotoNutrientCard("Protein", proteinValue, modifier = Modifier.weight(1f), icon = ImageVector.vectorResource(R.drawable.meat_svgrepo_com), iconTintColor = colorResource(id = R.color.proteinColor))
            PhotoNutrientCard("Fats", fatsValue, modifier = Modifier.weight(1f), icon = ImageVector.vectorResource(R.drawable.avocado), iconTintColor = colorResource(id = R.color.fatColor))
        }
    }
}

/**
 * A small Card for nutrient info (e.g. "Calories", "300").
 */
@Composable
fun RowScope.PhotoNutrientCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTintColor: Color = Color.Black
) {
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .weight(1f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = iconTintColor
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = label, style = MaterialTheme.typography.labelMedium)
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
fun HealthQualityBar(score: Int = 7,
                     maxScore: Int = 10,
                     rectCount: Int = 10) {
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.HeartBroken,
                    contentDescription = "Heart",
                    modifier = Modifier.size(48.dp),
                    tint = colorResource(id = R.color.sugarColor)
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Health score",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$score/$maxScore",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (i in 1..10) {
                        Box(
                            modifier = Modifier
                                .size(width = 16.dp, height = 8.dp)
                                .background(
                                    if (i <= score * 10 / maxScore) Color.Green else Color.LightGray
                                )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Simple capture button UI.
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
            color = androidx.compose.ui.graphics.Color.White,
            style = Stroke(width = 4.dp.toPx())
        )
        // Inner circle
        drawCircle(
            color = androidx.compose.ui.graphics.Color.White,
            radius = size.minDimension / 2.5f
        )
    }
}

/**
 * Helper extension to convert an ImageProxy's YUV to a Bitmap.
 */
fun ImageProxy.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer // Y
    val uBuffer = planes[1].buffer // U
    val vBuffer = planes[2].buffer // V

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

/**
 * Utility function to rotate a Bitmap by given degrees.
 */
fun rotateBitmap(source: Bitmap, rotationDegrees: Float): Bitmap {
    if (rotationDegrees == 0f) return source
    val matrix = android.graphics.Matrix().apply { postRotate(rotationDegrees) }
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}
