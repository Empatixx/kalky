package cz.krokviak.kalky.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.ui.components.KalkyCard

@Composable
fun BmiIndicatorCard(
    bmi: Float,
    textSize: TextUnit = 20.sp,
    modifier: Modifier = Modifier
) {
    val (label, labelColor) = bmiLabel(bmi)

    KalkyCard(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = AppTheme.colors.surface
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BMI",
                    color = AppTheme.colors.onBackground,
                    fontSize = textSize,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "%.1f".format(bmi),
                        color = AppTheme.colors.onBackground,
                        fontSize = textSize,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = label,
                        color = labelColor,
                        fontSize = textSize,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(999.dp))
            ) {
                BmiSegment(Color(0xFF4DA3FF))
                BmiSegment(Color(0xFF4ABF63))
                BmiSegment(Color(0xFFE3BE47))
                BmiSegment(Color(0xFFE98635))
            }
        }
    }
}

@Composable
private fun RowScope.BmiSegment(color: Color) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(8.dp)
            .padding(horizontal = 1.dp)
            .background(color, RoundedCornerShape(999.dp))
    )
}

private fun bmiLabel(bmi: Float): Pair<String, Color> {
    return when {
        bmi < 18.5f -> "Podváha" to Color(0xFF4DA3FF)
        bmi < 25f -> "Normální" to Color(0xFF4ABF63)
        bmi < 30f -> "Nadváha" to Color(0xFFE3BE47)
        else -> "Obezita" to Color(0xFFE98635)
    }
}
