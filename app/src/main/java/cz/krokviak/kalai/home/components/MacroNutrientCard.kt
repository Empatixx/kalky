package cz.krokviak.kalai.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.theme.AppTheme
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

@Composable
fun RowScope.MacroNutrientCard(
    amount: String,
    maxAmount: String,
    title: String,
    percentage: Float,
    iconResId: Int,
    donutColor: Color,
    modifier: Modifier = Modifier
) {
    CupertinoSection(
        modifier = modifier
            .border(
                width = 1.dp,
                color = AppTheme.colors.border,
                shape = RoundedCornerShape(16.dp)
            )
            .weight(1f),
        // add border
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 0.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CupertinoAutoscaleText(
                    text = title,
                    fontSize = 18.sp,
                    color = AppTheme.colors.onBackground,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                MacroNutrientDonutChart(
                    modifier = Modifier.fillMaxSize(),
                    percentage = percentage,
                    activeColor = donutColor,
                    centerIcon = ImageVector.vectorResource(iconResId),
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CupertinoText(
                    text = amount,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    color = AppTheme.colors.onBackground
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CupertinoText(
                    text = maxAmount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.onBackgroundSecondary
                )
            }
        }
    }
}
