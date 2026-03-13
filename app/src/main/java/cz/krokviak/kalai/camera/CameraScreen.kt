package cz.krokviak.kalai.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.barcode.BarcodeScanState
import cz.krokviak.kalai.barcode.BottomCard
import cz.krokviak.kalai.barcode.NutrientRow
import cz.krokviak.kalai.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalai.camera.components.CameraBottomControls
import cz.krokviak.kalai.camera.components.CameraPreview
import cz.krokviak.kalai.i18n.LocalStrings
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.ui.components.KalaiButton

@Composable
fun CameraScreen(
    cameraViewModel: CameraViewModel,
    uiState: CameraUiState,
    onPictureBytesReady: (ByteArray) -> Unit,
    onAddBarcodeClick: (OpenFoodFactsProduct, Int) -> Unit,
    onCloseClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            previewUseCase = uiState.previewUseCase,
            modifier = Modifier.fillMaxSize()
        )

        Surface(
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.35f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(52.dp)
        ) {
            IconButton(
                onClick = onCloseClick,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = LocalStrings.current.common.back,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        if (uiState.cameraMode == CameraMode.QR && uiState.barcodeScanState !is BarcodeScanState.Scanning) {
            BarcodeOverlay(
                state = uiState.barcodeScanState,
                onRetryClick = cameraViewModel::resetScan,
                onAddClick = onAddBarcodeClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        CameraBottomControls(
            cameraMode = uiState.cameraMode,
            onPhotoModeClick = { cameraViewModel.setMode(CameraMode.PHOTO) },
            onQrModeClick = { cameraViewModel.setMode(CameraMode.QR) },
            onCaptureClick = {
                cameraViewModel.takePicture(onPictureBytesReady)
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun BarcodeOverlay(
    state: BarcodeScanState,
    onRetryClick: () -> Unit,
    onAddClick: (OpenFoodFactsProduct, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current
    when (state) {
        is BarcodeScanState.Loading -> {
            BottomCard(modifier = modifier) {
                CircularProgressIndicator(
                    color = AppTheme.colors.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(s.barcode.searchingProduct)
            }
        }
        is BarcodeScanState.ProductFound -> {
            ProductFoundCard(
                state = state,
                onRetryClick = onRetryClick,
                onAddClick = onAddClick,
                modifier = modifier
            )
        }
        is BarcodeScanState.NotFound -> {
            BottomCard(modifier = modifier) {
                Text(
                    text = s.barcode.productNotFound,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                KalaiButton(
                    onClick = onRetryClick,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = AppTheme.colors.primary,
                    contentColor = AppTheme.colors.onPrimary
                ) {
                    Text(s.common.retry, fontWeight = FontWeight.Bold)
                }
            }
        }
        is BarcodeScanState.Error -> {
            BottomCard(modifier = modifier) {
                Text(
                    text = "${s.barcode.error}: ${state.message}",
                    fontSize = 16.sp,
                    color = Color.Red
                )
                Spacer(modifier = Modifier.height(12.dp))
                KalaiButton(
                    onClick = onRetryClick,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = AppTheme.colors.primary,
                    contentColor = AppTheme.colors.onPrimary
                ) {
                    Text(s.common.retry, fontWeight = FontWeight.Bold)
                }
            }
        }
        is BarcodeScanState.Scanning -> Unit
    }
}

@Composable
private fun ProductFoundCard(
    state: BarcodeScanState.ProductFound,
    onRetryClick: () -> Unit,
    onAddClick: (OpenFoodFactsProduct, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val product = state.product
    val nutriments = product.nutriments
    val defaultQuantity = product.servingSize?.filter { it.isDigit() }?.takeIf { it.isNotEmpty() }
        ?: product.productQuantity?.filter { it.isDigit() }?.takeIf { it.isNotEmpty() }
        ?: "100"
    var quantityText by remember(state.barcode) { mutableStateOf(defaultQuantity) }
    val quantity = quantityText.toIntOrNull() ?: 0
    val multiplier = quantity / 100.0

    val s = LocalStrings.current
    BottomCard(modifier = modifier) {
        Text(
            text = product.productName ?: s.common.unknownProduct,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = quantityText,
            onValueChange = { quantityText = it.filter { c -> c.isDigit() } },
            label = { Text(s.barcode.quantityGrams) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
        NutrientRow(s.common.calories, "${((nutriments?.energyKcal100g ?: 0.0) * multiplier).toInt()} kcal")
        NutrientRow(s.common.protein, "${((nutriments?.proteins100g ?: 0.0) * multiplier).toInt()} g")
        NutrientRow(s.common.fat, "${((nutriments?.fat100g ?: 0.0) * multiplier).toInt()} g")
        NutrientRow(s.common.carbs, "${((nutriments?.carbohydrates100g ?: 0.0) * multiplier).toInt()} g")

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KalaiButton(
                onClick = onRetryClick,
                modifier = Modifier.weight(1f),
                containerColor = AppTheme.colors.border,
                contentColor = AppTheme.colors.onBackground
            ) {
                Text(s.common.again, fontWeight = FontWeight.Bold)
            }
            KalaiButton(
                onClick = { onAddClick(product, quantity) },
                modifier = Modifier.weight(1f),
                containerColor = AppTheme.colors.primary,
                contentColor = AppTheme.colors.onPrimary
            ) {
                Text(s.common.add, fontWeight = FontWeight.Bold)
            }
        }
    }
}
