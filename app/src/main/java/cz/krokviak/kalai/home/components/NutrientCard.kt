package cz.krokviak.kalai.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RowScope.NutrientCard(
    amount: String,
    aboveDescription: String,
    belowDescription: String,
    percentage: Float,
    iconResId: Int,
    donutColor: Color
) {
    OutlinedCard(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
    ) {
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 0.dp
            ),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = aboveDescription,
                fontSize = 12.sp
            )
            Text(
                text = amount,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = belowDescription,
                fontSize = 12.sp
            )
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                DonutChart(
                    modifier = Modifier.fillMaxSize(),
                    percentage = percentage,
                    activeColor = donutColor,
                    centerIcon = ImageVector.vectorResource(iconResId),
                    centerIconSize = 24.dp,
                    holeRadius = 80f
                )
            }
        }
    }
}
