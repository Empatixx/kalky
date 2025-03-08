package cz.krokviak.kalai.camera.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.*
import cz.krokviak.kalai.R

@Composable
fun ButtonsSection(
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
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp),
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
