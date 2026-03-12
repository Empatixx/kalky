package cz.krokviak.kalai.nutrientedit.components

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
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.home.components.MacroNutrientDonutChart
import cz.krokviak.kalai.ui.components.KalaiCard

@Composable
fun VerticalCalorieCard(
    currentCalories: Int,
    calorieRatio: Float,
    modifier: Modifier = Modifier
) {
    KalaiCard(
        modifier = modifier
            .border(width = 1.dp, color = AppTheme.colors.border, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 24.dp, horizontal = 20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
        ) {
            Box(
                modifier = Modifier.size(112.dp),
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
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Denní cíl kalorií",
                color = AppTheme.colors.onBackgroundSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
