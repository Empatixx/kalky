package cz.krokviak.kalai.camera.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cz.krokviak.kalai.camera.CameraMode
import cz.krokviak.kalai.i18n.LocalStrings

@Composable
fun CameraBottomControls(
    cameraMode: CameraMode,
    onPhotoModeClick: () -> Unit,
    onQrModeClick: () -> Unit,
    onCaptureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 44.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val s = LocalStrings.current
        ModeToggleButton(
            isActive = cameraMode == CameraMode.PHOTO,
            onClick = onPhotoModeClick,
            imageVector = Icons.Outlined.PhotoCamera,
            contentDescription = s.camera.photoMode
        )

        CaptureButton(
            onClick = {
                if (cameraMode == CameraMode.PHOTO) onCaptureClick()
            },
            modifier = Modifier
                .alpha(if (cameraMode == CameraMode.PHOTO) 1f else 0.25f)
                .background(Color.Transparent, CircleShape)
        )

        ModeToggleButton(
            isActive = cameraMode == CameraMode.QR,
            onClick = onQrModeClick,
            imageVector = Icons.Outlined.QrCodeScanner,
            contentDescription = s.camera.qrMode
        )
    }
}

@Composable
private fun ModeToggleButton(
    isActive: Boolean,
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String
) {
    Surface(
        shape = CircleShape,
        color = if (isActive) Color.White else Color.White.copy(alpha = 0.25f),
        modifier = Modifier.size(56.dp)
    ) {
        IconButton(onClick = onClick) {
            CompositionLocalProvider(
                LocalContentColor provides if (isActive) Color.Black else Color.White
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
