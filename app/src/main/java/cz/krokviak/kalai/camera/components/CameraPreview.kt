package cz.krokviak.kalai.camera.components

import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraPreviewUI(
    previewUseCase: Preview?,
    onSurfaceProviderCreated: (PreviewView) -> Unit,
    onCaptureClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera feed
        AndroidView(
            factory = { context -> PreviewView(context) },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                onSurfaceProviderCreated(previewView)
            }
        )

        // Capture button
        CaptureButton(
            onClick = onCaptureClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}
