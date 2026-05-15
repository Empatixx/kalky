package cz.krokviak.kalky.scenes.nutrientedit.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.core.ui.LocalDimensions
import cz.krokviak.kalky.scenes.home.components.MacroNutrientDonutChart
import cz.krokviak.kalky.core.ui.components.KalkyCard

@Composable
fun VerticalCalorieCard(
    currentCalories: Int,
    calorieRatio: Float,
    modifier: Modifier = Modifier
) {
    val dims = LocalDimensions.current
    KalkyCard(
        modifier = modifier
            .border(width = 1.dp, color = AppTheme.colors.border, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = dims.cardPadding * 1.5f, horizontal = dims.cardPadding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
        ) {
            Box(
                modifier = Modifier.size(dims.donutChartSize * 0.9f),
                contentAlignment = Alignment.Center
            ) {
                MacroNutrientDonutChart(
                    modifier = Modifier.matchParentSize(),
                    percentage = calorieRatio,
                    activeColor = AppTheme.colors.primary,
                    centerIcon = Icons.Outlined.LocalFireDepartment,
                )
            }
            Text(
                text = "${currentCalories} kcal",
                color = AppTheme.colors.onBackground,
                fontSize = dims.fontHero,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = LocalStrings.current.nutrientEdit.dailyCalorieTarget,
                color = AppTheme.colors.onBackgroundSecondary,
                fontSize = dims.fontBody,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
