package cz.krokviak.kalai.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.home.MainUiState
import io.github.alexzhirkevich.cupertino.section.CupertinoSection
import kotlin.math.absoluteValue

@Composable
fun CalorieCard(
    uiState: MainUiState
) {
    CupertinoSection(
        modifier = Modifier.border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(32.dp)).fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Row(
            modifier = Modifier.padding(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${uiState.calorieDifference().absoluteValue}",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = calorieLabel(uiState.calorieDifference()))
            }

            // Right side: Donut chart
            Box(
                modifier = Modifier.size(125.dp),
                contentAlignment = Alignment.Center
            ) {
                MacroNutrientDonutChart(
                    modifier = Modifier.fillMaxSize(),
                    percentage = uiState.calorieRatio(),
                    activeColor = Color.Black,
                    centerIcon = Icons.Outlined.LocalFireDepartment,
                    centerIconSize = 32.dp,
                    holeRadius = 80f
                )
            }
        }
    }
}

fun calorieLabel(calDifference: Int): String {
    val label = if (calDifference > 0) "zbývá kcal" else "přesazeno kcal"
    return label;
}