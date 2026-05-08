package cz.krokviak.kalky.scenes.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cz.krokviak.kalky.core.theme.MacroColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.core.ui.LocalDimensions
import cz.krokviak.kalky.core.ui.components.KalkyCard


@Composable
fun FoodHealthQualityCard(
    score: Int = 0,
    maxScore: Int = 10
){
    val s = LocalStrings.current
    KalkyCard(
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .border(width = 1.dp, color = AppTheme.colors.border, shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)).fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .background(AppTheme.colors.surfaceSecondary, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.HeartBroken,
                    contentDescription = s.common.cdHeart,
                    modifier = Modifier.size(48.dp),
                    tint = MacroColors.sugar
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = s.detail.healthQuality,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$score/$maxScore",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                FoodHealthQualityBar(
                    score = score,
                    maxScore = maxScore,
                    rectCount = 10
                )
            }
        }
    }
}

@Composable
private fun FoodHealthQualityBar(
    score: Int,
    maxScore: Int,
    rectCount: Int
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 1..rectCount) {
            // highlight up to (score * rectCount / maxScore)
            val threshold = score * rectCount / maxScore
            Box(
                modifier = Modifier
                    .size(width = 16.dp, height = 8.dp)
                    .background(
                        if (i <= threshold) Color.Green else AppTheme.colors.border
                    )
            )
        }
    }
}
