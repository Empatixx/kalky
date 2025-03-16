package cz.krokviak.kalai.nutrientedit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.R
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.section.CupertinoSection
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun MacroNutrientRatioDonutChart(
    protein: Int = 0,
    carbs: Int = 0,
    fat: Int = 0,
    modifier: Modifier = Modifier
) {
    CupertinoSection(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp)),
        contentPadding = PaddingValues(0.dp),
        dividerPadding = PaddingValues(0.dp),
    ) {
        MacroNutrientDonutSection(protein = protein, carbs = carbs, fat = fat)
    }
}

@Composable
fun MacroNutrientDonutSection(
    protein: Int,
    carbs: Int,
    fat: Int,
    modifier: Modifier = Modifier
) {
    val total = (protein + carbs + fat).toFloat().takeIf { it > 0f } ?: 1f // avoid divide by zero
    val proteinPercent = remember(protein, carbs, fat) { protein / total * 100 }
    val carbsPercent = remember(protein, carbs, fat) { carbs / total * 100 }
    val fatPercent = remember(protein, carbs, fat) { fat / total * 100 }


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Legend row
        MacroNutrientLegendRow(
            proteinPercent = proteinPercent,
            carbsPercent = carbsPercent,
            fatPercent = fatPercent
        )
        val proteinColor = colorResource(id = R.color.proteinColor)
        val carbsColor = colorResource(id = R.color.carbsColor)
        val fatColor = colorResource(id = R.color.fatColor)
        val data by remember(proteinPercent, carbsPercent, fatPercent) {
            mutableStateOf(
                listOf(
                    Pie(label = "Bílkoviny", data = proteinPercent.toDouble(), color = proteinColor),
                    Pie(label = "Sacharidy", data = carbsPercent.toDouble(), color = carbsColor),
                    Pie(label = "Tuky", data = fatPercent.toDouble(), color =   fatColor)
                )
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Occupies remaining space
            contentAlignment = Alignment.Center
        ) {
            PieChart(
                modifier = Modifier.fillMaxSize()
                    .rotate(270f),
                data = data,
                selectedScale = 1.0f,
                style = Pie.Style.Stroke(width = 30.dp)
            )
        }
    }
}

@Composable
private fun MacroNutrientLegendRow(
    proteinPercent: Float,
    carbsPercent: Float,
    fatPercent: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(
            label = "Bílkoviny",
            percent = proteinPercent,
            colorId = R.color.proteinColor
        )
        LegendItem(
            label = "Sacharidy",
            percent = carbsPercent,
            colorId = R.color.carbsColor
        )
        LegendItem(
            label = "Tuky",
            percent = fatPercent,
            colorId = R.color.fatColor
        )
    }
}

@Composable
private fun LegendItem(
    label: String,
    percent: Float,
    colorId: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(end = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(colorResource(id = colorId))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = String.format("%.0f%%", percent),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        Text(
            text = label,
            fontSize = 12.sp
        )
    }
}