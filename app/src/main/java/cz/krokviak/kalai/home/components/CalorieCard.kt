package cz.krokviak.kalai.home.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.home.MainUiState
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.section.CupertinoSection
import kotlin.math.absoluteValue

@Composable
fun CalorieCard(
    currentCalories: Int,
    maxCalories: Int,
    calorieRatio: Float,
    modifier: Modifier = Modifier
) {
    CupertinoSection(
        modifier = modifier.border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(32.dp)).fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Row(
            modifier = Modifier.padding(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Right side: Donut chart
            Box(
                modifier = Modifier.size(125.dp),
                contentAlignment = Alignment.Center
            ) {
                MacroNutrientDonutChart(
                    modifier = Modifier.fillMaxSize(),
                    percentage = calorieRatio,
                    activeColor = Color.Black,
                    centerIcon = Icons.Outlined.LocalFireDepartment,
                    centerIconSize = 32.dp,
                    holeRadius = 80f
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                CupertinoText(
                    text = "${currentCalories} kcal",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                CupertinoText(
                    text = "${maxCalories} kcal",
                    fontSize = 16.sp,
                )
            }

        }
    }
}

fun calorieLabel(calDifference: Int): String {
    val label = if (calDifference > 0) "zbývá kcal" else "přesazeno kcal"
    return label;
}