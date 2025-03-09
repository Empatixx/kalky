package cz.krokviak.kalai.camera.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
                type = analysisData?.foodType ?: "Neznámý",
                fontSize = 12.sp
            )

            Row{
                Text(
                    text = analysisData?.title ?: "Neznámý",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(0.65f)
                        .padding(start = 8.dp, end = 8.dp)
                )
                AmountSelector(
                    amount = portion,
                    onIncrease = onIncreasePortion,
                    onDecrease = onDecreasePortion,
                    modifier = Modifier.weight(0.35f)
                )
            }

            NutrientsGrid(
                caloriesValue = analysisData?.calories?.toString() ?: "0",
                carbsValue = analysisData?.carbs?.toString()?.plus("g") ?: "0g",
                proteinValue = analysisData?.protein?.toString()?.plus("g") ?: "0g",
                fatsValue = analysisData?.fat?.toString()?.plus("g") ?: "0g"
            )

            HealthQualityBar(
                score = analysisData?.healthScore ?: 0,
                maxScore = 10
            )

            ButtonsSection(
                onFixResults = onFixResults,
                onConfirm = onConfirm
            )
        }
    }
}
