package cz.krokviak.kalai.camera.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.camera.data.FoodAnalysisDto

@Composable
fun BottomSheetCard(
    modifier: Modifier = Modifier,
    analysisData: FoodAnalysisDto?,
    portion: Int,
    onIncreasePortion: () -> Unit,
    onDecreasePortion: () -> Unit,
    onFixResults: () -> Unit,
    onConfirm: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FoodTypeBadge(
                type = analysisData?.foodType ?: "Unknown",
                fontSize = 12.sp
            )

            TitleAndAmountRow(
                title = analysisData?.title ?: "Pancakes with blueberries & syrup",
                amount = portion,
                onIncrease = onIncreasePortion,
                onDecrease = onDecreasePortion
            )

            NutrientsGrid(
                caloriesValue = analysisData?.calories?.toString() ?: "350",
                carbsValue = analysisData?.carbs?.toString()?.plus("g") ?: "45g",
                proteinValue = analysisData?.protein?.toString()?.plus("g") ?: "12g",
                fatsValue = analysisData?.fat?.toString()?.plus("g") ?: "10g"
            )

            HealthQualityBar(
                score = analysisData?.healthScore ?: 7,
                maxScore = 10
            )

            ButtonsSection(
                onFixResults = onFixResults,
                onConfirm = onConfirm
            )
        }
    }
}
