package cz.krokviak.kalai.nutrientedit.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.home.MainUiState
import cz.krokviak.kalai.home.components.MacroNutrientDonutChart
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

@Composable
fun VerticalCalorieCard(
    currentCalories: Int,
    calorieRatio: Float,
    modifier: Modifier = Modifier
) {
    CupertinoSection(
        modifier = modifier
            .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(32.dp))
            .fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Top: Donut (pie) chart
            Box(
                modifier = Modifier.size(125.dp),
                contentAlignment = Alignment.Center
            ) {
                MacroNutrientDonutChart(
                    modifier = Modifier.matchParentSize(), // Chart fills the Box size
                    percentage = calorieRatio,
                    activeColor = Color.Black,
                    centerIcon = Icons.Outlined.LocalFireDepartment,
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            // Bottom: Total calories text
            CupertinoText(
                text = "${currentCalories} kcal",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}