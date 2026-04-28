package cz.krokviak.kalky.customfood

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cz.krokviak.kalky.theme.MacroColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.i18n.LocalStrings
import cz.krokviak.kalky.ui.components.MacroPickerRow
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.ui.LocalDimensions
import cz.krokviak.kalky.ui.components.KalkyButton
import cz.krokviak.kalky.ui.components.KalkyCard

private enum class ManualMacroPickerField { PROTEIN, CARBS, FAT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualFoodEntryScene(
    viewModel: ManualFoodEntryViewModel,
    onBackClick: () -> Unit,
    onFoodAdded: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val s = LocalStrings.current
    var activePickerField by remember { mutableStateOf<ManualMacroPickerField?>(null) }

    LaunchedEffect(Unit) {
        viewModel.foodAdded.collect {
            onFoodAdded()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = s.customFood.manualEntryTitle,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.onBackground
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = s.common.back,
                        tint = AppTheme.colors.onBackground
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        val dims = LocalDimensions.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dims.screenPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dims.itemSpacing)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                placeholder = {
                    Text(
                        text = s.customFood.foodName,
                        color = AppTheme.colors.onBackgroundSecondary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppTheme.colors.onBackgroundSecondary,
                    unfocusedBorderColor = AppTheme.colors.border,
                    focusedContainerColor = AppTheme.colors.surface,
                    unfocusedContainerColor = AppTheme.colors.surface,
                    focusedTextColor = AppTheme.colors.onBackground,
                    unfocusedTextColor = AppTheme.colors.onBackground
                ),
                singleLine = true
            )

            // Calories summary card (read-only, auto-calculated)
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
                            contentDescription = "Calories",
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
                        text = state.calories.toString(),
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

            // Macro editors
            KalkyCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = AppTheme.colors.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MacroPickerRow(
                        label = s.common.protein,
                        value = state.protein,
                        icon = Icons.Default.Restaurant,
                        activeColor = MacroColors.protein,
                        expanded = activePickerField == ManualMacroPickerField.PROTEIN,
                        onClick = {
                            activePickerField =
                                if (activePickerField == ManualMacroPickerField.PROTEIN) null
                                else ManualMacroPickerField.PROTEIN
                        },
                        onValueChange = viewModel::onProteinChange,
                        pickerItemHeight = 28.dp,
                        pickerVisibleItemsCount = 3,
                        pickerTextSize = 17.sp,
                        pickerHorizontalPadding = 8.dp,
                        pickerBottomPadding = 2.dp
                    )
                    MacroDivider()
                    MacroPickerRow(
                        label = s.common.carbs,
                        value = state.carbs,
                        icon = Icons.Default.Spa,
                        activeColor = MacroColors.carbs,
                        expanded = activePickerField == ManualMacroPickerField.CARBS,
                        onClick = {
                            activePickerField =
                                if (activePickerField == ManualMacroPickerField.CARBS) null
                                else ManualMacroPickerField.CARBS
                        },
                        onValueChange = viewModel::onCarbsChange,
                        pickerItemHeight = 28.dp,
                        pickerVisibleItemsCount = 3,
                        pickerTextSize = 17.sp,
                        pickerHorizontalPadding = 8.dp,
                        pickerBottomPadding = 2.dp
                    )
                    MacroDivider()
                    MacroPickerRow(
                        label = s.common.fat,
                        value = state.fat,
                        icon = Icons.Default.Eco,
                        activeColor = MacroColors.fat,
                        expanded = activePickerField == ManualMacroPickerField.FAT,
                        onClick = {
                            activePickerField =
                                if (activePickerField == ManualMacroPickerField.FAT) null
                                else ManualMacroPickerField.FAT
                        },
                        onValueChange = viewModel::onFatChange,
                        pickerItemHeight = 28.dp,
                        pickerVisibleItemsCount = 3,
                        pickerTextSize = 17.sp,
                        pickerHorizontalPadding = 8.dp,
                        pickerBottomPadding = 2.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            KalkyButton(
                onClick = { viewModel.submit() },
                modifier = Modifier.fillMaxWidth(),
                containerColor = if (state.name.isBlank()) Color.Gray else Color.Black
            ) {
                Text(
                    text = s.customFood.save,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MacroDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(start = 56.dp, end = 14.dp)
            .background(AppTheme.colors.border)
    )
}
