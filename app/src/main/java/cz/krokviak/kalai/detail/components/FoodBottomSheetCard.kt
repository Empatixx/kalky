package cz.krokviak.kalai.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.R
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.ui.components.KalaiCard


@Composable
fun BoxScope.FoodBottomSheetCard(
    name: String,
    portion: Int,
    onIncreasePortion: () -> Unit,
    onDecreasePortion: () -> Unit,
    calories: Int,
    protein: Int,
    fats: Int,
    carbs: Int,
    healthScore: Int,
    modifier: Modifier = Modifier,
    onFixResult: () -> Unit,
    onFinish: () -> Unit
) {
    KalaiCard(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .align(Alignment.BottomCenter),
        color = AppTheme.colors.surfaceSecondary,
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ){
                TitleRow(
                    name = name,
                    portion = portion,
                    onIncreasePortion = onIncreasePortion,
                    onDecreasePortion = onDecreasePortion
                )
                NutrientsGrid(
                    calories = calories,
                    protein = protein,
                    fats = fats,
                    carbs = carbs
                )
                FoodHealthQualityCard(
                    score = healthScore,
                    maxScore = 10,
                )
            }
                FoodDetailButtons(
                    onFixResult = onFixResult,
                    onFinish = onFinish,
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                )

        }
    }
}

@Composable
fun NutrientsGrid(
    calories: Int,
    protein: Int,
    fats: Int,
    carbs: Int
) {
    val carbsValue = remember(carbs) { "$carbs g" }
    val proteinValue = remember(protein) { "$protein g" }
    val fatsValue = remember(fats) { "$fats g" }
    val caloriesValue = remember(calories) { "$calories kcal" }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PhotoNutrientCard(
                "Kalorie",
                caloriesValue,
                icon = Icons.Outlined.LocalFireDepartment,
                modifier = Modifier.weight(1f)
            )
            PhotoNutrientCard(
                "Sacharidy", carbsValue,
                icon = ImageVector.vectorResource(R.drawable.wheat),
                iconTintColor = colorResource(id = R.color.carbsColor),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PhotoNutrientCard(
                "Bilkoviny",
                proteinValue,
                icon = ImageVector.vectorResource(R.drawable.chicken_leg),
                iconTintColor = colorResource(id = R.color.proteinColor),
                modifier = Modifier.weight(1f)
            )
            PhotoNutrientCard(
                "Tuky", fatsValue,
                icon = ImageVector.vectorResource(R.drawable.avocado),
                iconTintColor = colorResource(id = R.color.fatColor),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TitleRow(
    name: String,
    portion: Int,
    onIncreasePortion: () -> Unit,
    onDecreasePortion: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Row {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .weight(0.65f)
                    .align(Alignment.CenterVertically)
            )
            PortionPicker(
                amount = portion,
                onIncrease = onIncreasePortion,
                onDecrease = onDecreasePortion,
                modifier = Modifier.weight(0.35f)
            )
        }
    }
}
