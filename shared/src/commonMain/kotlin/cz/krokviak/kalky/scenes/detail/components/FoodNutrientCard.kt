package cz.krokviak.kalky.scenes.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import cz.krokviak.kalky.core.ui.LocalDimensions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.core.ui.components.KalkyCard

@Composable
fun PhotoNutrientCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconTintColor: Color = AppTheme.colors.onBackground,
    modifier: Modifier = Modifier
) {
    KalkyCard(
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .border(width = 1.dp, color = AppTheme.colors.border, shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)).fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(AppTheme.colors.surfaceSecondary, RoundedCornerShape(LocalDimensions.current.cardCornerRadius))
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
