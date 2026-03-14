package cz.krokviak.kalai.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.theme.AppTheme

data class KalaiContextMenuItem(
    val label: String,
    val icon: ImageVector,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit
)

@Composable
fun KalaiContextMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<KalaiContextMenuItem>,
    modifier: Modifier = Modifier
) {
    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(
            extraSmall = RoundedCornerShape(14.dp)
        )
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = modifier.widthIn(min = 160.dp),
            containerColor = AppTheme.colors.surface,
            shadowElevation = 8.dp
        ) {
            items.forEachIndexed { index, item ->
                val itemColor = if (item.isDestructive) Color.Red else AppTheme.colors.onBackground
                Row(
                    modifier = Modifier
                        .clickable {
                            onDismissRequest()
                            item.onClick()
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .widthIn(min = 160.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(20.dp),
                        tint = itemColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item.label,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = itemColor
                    )
                }
                if (index < items.lastIndex) {
                    HorizontalDivider(
                        color = AppTheme.colors.border,
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}
