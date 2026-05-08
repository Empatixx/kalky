package cz.krokviak.kalky.scenes.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import cz.krokviak.kalky.core.theme.MacroColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.scenes.nutrientedit.MacroField
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.core.ui.LocalDimensions
import cz.krokviak.kalky.core.ui.components.KalkyCard
import cz.krokviak.kalky.core.ui.components.MacroPickerRow

@Composable
fun BoxScope.FoodBottomSheetCard(
    name: String,
    calories: Int,
    protein: Int,
    fats: Int,
    carbs: Int,
    activeField: MacroField?,
    modifier: Modifier = Modifier,
    onFixResult: () -> Unit,
    onFinish: () -> Unit,
    onProteinChange: (Int) -> Unit,
    onCarbsChange: (Int) -> Unit,
    onFatChange: (Int) -> Unit,
    onToggleField: (MacroField) -> Unit,
) {
    val sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)

    KalkyCard(
        modifier = modifier
            .fillMaxSize()
            .align(Alignment.BottomCenter),
        shape = sheetShape,
        color = AppTheme.colors.surfaceSecondary,
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SheetHandle()
                TitleRow(name = name)
                CaloriesSummaryCard(calories = calories)

                KalkyCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
                    color = AppTheme.colors.surface
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val s = LocalStrings.current
                        MacroPickerRow(
                            label = s.common.protein,
                            value = protein,
                            icon = Icons.Default.Restaurant,
                            activeColor = MacroColors.protein,
                            expanded = activeField == MacroField.PROTEIN,
                            onClick = { onToggleField(MacroField.PROTEIN) },
                            onValueChange = onProteinChange,
                            pickerItemHeight = 28.dp,
                            pickerVisibleItemsCount = 3,
                            pickerTextSize = 17.sp,
                            pickerHorizontalPadding = 8.dp,
                            pickerBottomPadding = 2.dp
                        )
                        DetailGroupDivider()
                        MacroPickerRow(
                            label = s.common.carbs,
                            value = carbs,
                            icon = Icons.Default.Spa,
                            activeColor = MacroColors.carbs,
                            expanded = activeField == MacroField.CARBS,
                            onClick = { onToggleField(MacroField.CARBS) },
                            onValueChange = onCarbsChange,
                            pickerItemHeight = 28.dp,
                            pickerVisibleItemsCount = 3,
                            pickerTextSize = 17.sp,
                            pickerHorizontalPadding = 8.dp,
                            pickerBottomPadding = 2.dp
                        )
                        DetailGroupDivider()
                        MacroPickerRow(
                            label = s.common.fat,
                            value = fats,
                            icon = Icons.Default.Eco,
                            activeColor = MacroColors.fat,
                            expanded = activeField == MacroField.FAT,
                            onClick = { onToggleField(MacroField.FAT) },
                            onValueChange = onFatChange,
                            pickerItemHeight = 28.dp,
                            pickerVisibleItemsCount = 3,
                            pickerTextSize = 17.sp,
                            pickerHorizontalPadding = 8.dp,
                            pickerBottomPadding = 2.dp
                        )
                    }
                }
            }

            FoodDetailButtons(
                onFixResult = onFixResult,
                onFinish = onFinish,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SheetHandle() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(AppTheme.colors.border)
        )
    }
}

@Composable
private fun DetailGroupDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(start = 56.dp, end = 14.dp)
            .background(AppTheme.colors.border)
    )
}

@Composable
fun TitleRow(
    name: String
) {
    val dims = LocalDimensions.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            color = AppTheme.colors.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = dims.fontTitle,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CaloriesSummaryCard(calories: Int) {
    val dims = LocalDimensions.current
    KalkyCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = AppTheme.colors.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dims.rowHeight)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(dims.iconCircleSize)
                    .background(
                        color = Color.Black,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = LocalStrings.current.common.calories,
                    tint = Color.White,
                    modifier = Modifier.size(dims.iconSize)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = LocalStrings.current.common.calories,
                color = AppTheme.colors.onBackground,
                fontSize = dims.fontBody,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = calories.toString(),
                color = AppTheme.colors.onBackground,
                fontSize = dims.fontBody,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = LocalStrings.current.common.kcal,
                color = AppTheme.colors.onBackgroundSecondary,
                fontSize = dims.fontBody,
                fontWeight = FontWeight.SemiBold
            )
            // Keep the same trailing width as nutrient rows (spacer + chevron),
            // so unit text aligns with the "g" column.
            Spacer(modifier = Modifier.width(26.dp))
        }
    }
}
