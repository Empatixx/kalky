package cz.krokviak.kalky.scenes.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.core.ui.LocalDimensions
import cz.krokviak.kalky.core.ui.components.KalkyCard

@Composable
fun CalorieCard(
    currentCalories: Int,
    maxCalories: Int,
    calorieRatio: Float,
    modifier: Modifier = Modifier
) {
    val dims = LocalDimensions.current

    KalkyCard(
        modifier = modifier.border(width = 1.dp, color = AppTheme.colors.border, shape = RoundedCornerShape(32.dp)).fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Row(
            modifier = Modifier.padding(dims.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(dims.donutChartSize),
                contentAlignment = Alignment.Center
            ) {
                MacroNutrientDonutChart(
                    modifier = Modifier.fillMaxSize(),
                    percentage = calorieRatio,
                    activeColor = AppTheme.colors.primary,
                    centerIcon = Icons.Outlined.LocalFireDepartment,
                )
            }
            Spacer(modifier = Modifier.size(dims.halfSpacing))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${currentCalories} kcal",
                    fontSize = dims.fontHero,
                    lineHeight = (dims.fontHero.value * 1.15f).sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppTheme.colors.onBackground
                )
                Text(
                    text = "${maxCalories} kcal",
                    fontSize = dims.fontSubtitle,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.onBackgroundSecondary
                )
            }
        }
    }
}
