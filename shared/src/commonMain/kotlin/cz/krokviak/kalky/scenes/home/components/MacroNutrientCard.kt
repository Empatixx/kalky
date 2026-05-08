package cz.krokviak.kalky.scenes.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.core.ui.LocalDimensions
import cz.krokviak.kalky.core.ui.components.KalkyCard

@Composable
fun RowScope.MacroNutrientCard(
    amount: String,
    maxAmount: String,
    title: String,
    percentage: Float,
    icon: ImageVector,
    donutColor: Color,
    modifier: Modifier = Modifier
) {
    val dims = LocalDimensions.current
    KalkyCard(
        modifier = modifier
            .border(
                width = 1.dp,
                color = AppTheme.colors.border,
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            )
            .weight(1f),
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
        contentPadding = PaddingValues(0.dp),
    ) {
        Column(
            modifier = Modifier.padding(
                start = dims.cardPadding,
                top = 0.dp,
                end = dims.cardPadding,
                bottom = dims.halfSpacing * 1.5f
            ),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dims.halfSpacing),
                contentAlignment = Alignment.Center
            ) {
                CupertinoAutoscaleText(
                    text = title,
                    fontSize = dims.fontBody,
                    color = AppTheme.colors.onBackground,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                MacroNutrientDonutChart(
                    modifier = Modifier.fillMaxSize(),
                    percentage = percentage,
                    activeColor = donutColor,
                    centerIcon = icon,
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = amount,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    fontSize = dims.fontSubtitle,
                    color = AppTheme.colors.onBackground
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = maxAmount,
                    fontSize = dims.fontSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.onBackgroundSecondary
                )
            }
        }
    }
}
