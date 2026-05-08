package cz.krokviak.kalky.scenes.settings.components

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
import cz.krokviak.kalky.core.common.formatFloat1
import cz.krokviak.kalky.core.common.utils.BmiCategory
import cz.krokviak.kalky.core.common.utils.bmiCategory
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.core.theme.KalkyAccents
import cz.krokviak.kalky.core.ui.components.KalkyCard

@Composable
fun BmiIndicatorCard(
    bmi: Float,
    textSize: TextUnit = 20.sp,
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current
    val category = bmiCategory(bmi)
    val label = when (category) {
        BmiCategory.UNDERWEIGHT -> s.profile.bmiUnderweight
        BmiCategory.NORMAL -> s.profile.bmiNormal
        BmiCategory.OVERWEIGHT -> s.profile.bmiOverweight
        BmiCategory.OBESE -> s.profile.bmiObese
    }
    val labelColor = when (category) {
        BmiCategory.UNDERWEIGHT -> KalkyAccents.bmiUnder
        BmiCategory.NORMAL -> KalkyAccents.bmiNormal
        BmiCategory.OVERWEIGHT -> KalkyAccents.bmiOver
        BmiCategory.OBESE -> KalkyAccents.bmiObese
    }

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
                        text = formatFloat1(bmi),
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
                BmiSegment(KalkyAccents.bmiUnder)
                BmiSegment(KalkyAccents.bmiNormal)
                BmiSegment(KalkyAccents.bmiOver)
                BmiSegment(KalkyAccents.bmiObese)
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

