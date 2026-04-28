package cz.krokviak.kalky.customfood.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalky.customfood.scaledTo
import cz.krokviak.kalky.i18n.LocalStrings
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.theme.MacroColors
import cz.krokviak.kalky.ui.LocalDimensions
import cz.krokviak.kalky.ui.components.KalkyButton
import cz.krokviak.kalky.ui.components.KalkyCard
import cz.krokviak.kalky.ui.components.MacroPickerRow
import kotlinx.collections.immutable.persistentListOf

private val PORTION_PRESETS = persistentListOf(50, 100, 150, 200, 250)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortionPickerSheet(
    product: OpenFoodFactsProduct,
    portionGrams: Int,
    onPortionChanged: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalStrings.current
    val scaled = product.nutriments.scaledTo(portionGrams)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = AppTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = product.productName ?: s.common.unknownProduct,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.onBackground
            )

            Text(
                text = s.customFood.portionSize,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.onBackgroundSecondary
            )
            OutlinedTextField(
                value = portionGrams.toString(),
                onValueChange = { text ->
                    val grams = text.filter { it.isDigit() }.toIntOrNull() ?: 0
                    onPortionChanged(grams)
                },
                suffix = { Text(s.customFood.grams, color = AppTheme.colors.onBackgroundSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppTheme.colors.onBackgroundSecondary,
                    unfocusedBorderColor = AppTheme.colors.border,
                    focusedContainerColor = AppTheme.colors.surface,
                    unfocusedContainerColor = AppTheme.colors.surface,
                    focusedTextColor = AppTheme.colors.onBackground,
                    unfocusedTextColor = AppTheme.colors.onBackground
                )
            )

            PortionPresetRow(
                portionGrams = portionGrams,
                onPortionChanged = onPortionChanged
            )

            CaloriesRow(calories = scaled.calories)

            KalkyCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = AppTheme.colors.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MacroPickerRow(
                        label = s.common.protein,
                        value = scaled.protein,
                        icon = Icons.Default.Restaurant,
                        activeColor = MacroColors.protein,
                        expanded = false,
                        onClick = {},
                        onValueChange = {},
                        enabled = false
                    )
                    PortionGroupDivider()
                    MacroPickerRow(
                        label = s.common.carbs,
                        value = scaled.carbs,
                        icon = Icons.Default.Spa,
                        activeColor = MacroColors.carbs,
                        expanded = false,
                        onClick = {},
                        onValueChange = {},
                        enabled = false
                    )
                    PortionGroupDivider()
                    MacroPickerRow(
                        label = s.common.fat,
                        value = scaled.fat,
                        icon = Icons.Default.Eco,
                        activeColor = MacroColors.fat,
                        expanded = false,
                        onClick = {},
                        onValueChange = {},
                        enabled = false
                    )
                }
            }

            KalkyButton(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = s.common.add,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun PortionPresetRow(
    portionGrams: Int,
    onPortionChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PORTION_PRESETS.forEach { grams ->
            val isSelected = portionGrams == grams
            KalkyCard(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onPortionChanged(grams) },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) AppTheme.colors.onBackground else AppTheme.colors.surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${grams}g",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) AppTheme.colors.background else AppTheme.colors.onBackground
                    )
                }
            }
        }
    }
}

@Composable
private fun CaloriesRow(calories: Int) {
    val s = LocalStrings.current
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
                    .background(color = Color.Black, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(dims.iconSize)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = s.common.calories,
                color = AppTheme.colors.onBackground,
                fontSize = dims.fontBody,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = calories.toString(),
                color = AppTheme.colors.onBackground,
                fontSize = dims.fontBody,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "kcal",
                color = AppTheme.colors.onBackgroundSecondary,
                fontSize = dims.fontBody,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(26.dp))
        }
    }
}

@Composable
private fun PortionGroupDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(start = 56.dp, end = 14.dp)
            .background(AppTheme.colors.border)
    )
}
