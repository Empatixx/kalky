package cz.krokviak.kalai.nutrientedit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.ui.LocalDimensions

@Composable
fun NutrientEditRow(
    label: String,
    value: Int,
    valueUnit: String,
    icon: ImageVector,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dims = LocalDimensions.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dims.rowHeight)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(dims.iconCircleSize)
                .background(color = activeColor.copy(alpha = 0.14f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = activeColor,
                modifier = Modifier.size(dims.iconSize)
            )
        }

        Text(
            text = label,
            color = AppTheme.colors.onBackground,
            fontSize = dims.fontBody,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value.toString(),
            color = AppTheme.colors.onBackground,
            fontSize = dims.fontBody,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = valueUnit,
            color = AppTheme.colors.onBackgroundSecondary,
            fontSize = dims.fontBody,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.width(2.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = AppTheme.colors.onBackgroundSecondary
        )
    }
}
