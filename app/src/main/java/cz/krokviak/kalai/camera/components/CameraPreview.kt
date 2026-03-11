package cz.krokviak.kalai.camera.components

import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraPreview(
    previewUseCase: Preview?,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context -> PreviewView(context) },
        modifier = modifier.fillMaxSize(),
        update = { previewView ->
            previewUseCase?.setSurfaceProvider(previewView.surfaceProvider)
        }
    )
}
