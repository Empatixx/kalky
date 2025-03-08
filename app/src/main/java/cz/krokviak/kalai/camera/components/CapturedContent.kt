package cz.krokviak.kalai.camera.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cz.krokviak.kalai.camera.data.FoodAnalysisDto

@Composable
fun CapturedContentUI(
    bitmap: Bitmap,
    analysisData: FoodAnalysisDto?,
    portion: Int,
    onIncreasePortion: () -> Unit,
    onDecreasePortion: () -> Unit,
    onFixResults: () -> Unit,
    onConfirm: () -> Unit
) {
    val bottomSheetHeight = 450.dp
    val offset = bottomSheetHeight / 2

    Box(modifier = Modifier.fillMaxSize()) {
        // Show the captured image behind
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .offset(y = -offset),
            contentScale = ContentScale.Crop
        )

        // The "bottom sheet"
        BottomSheetCard(
            analysisData = analysisData,
            portion = portion,
            onIncreasePortion = onIncreasePortion,
            onDecreasePortion = onDecreasePortion,
            onFixResults = onFixResults,
            onConfirm = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomSheetHeight)
                .align(Alignment.BottomCenter)
        )
    }
}
