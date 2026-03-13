package cz.krokviak.kalai.customfood

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
import androidx.compose.material.icons.filled.LocalFireDepartment
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.R
import cz.krokviak.kalai.i18n.LocalStrings
import cz.krokviak.kalai.nutrientedit.components.NutrientEditRow
import cz.krokviak.kalai.settings.components.IosInlineValuePicker
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.ui.components.KalaiButton
import cz.krokviak.kalai.ui.components.KalaiCard

private enum class ManualMacroPickerField { PROTEIN, CARBS, FAT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualFoodEntryScene(
    viewModel: CustomFoodViewModel,
    onBackClick: () -> Unit,
    onFoodAdded: () -> Unit
) {
    val state by viewModel.manualEntryState.collectAsState()
    val s = LocalStrings.current
    val macroValues = remember { (0..500).map { it.toString() } }
    var activePickerField by remember { mutableStateOf<ManualMacroPickerField?>(null) }
    var selectedProteinIndex by remember { mutableIntStateOf(0) }
    var selectedCarbsIndex by remember { mutableIntStateOf(0) }
    var selectedFatIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(state.protein) {
        selectedProteinIndex = state.protein.coerceIn(0, macroValues.lastIndex)
    }
    LaunchedEffect(state.carbs) {
        selectedCarbsIndex = state.carbs.coerceIn(0, macroValues.lastIndex)
    }
    LaunchedEffect(state.fat) {
        selectedFatIndex = state.fat.coerceIn(0, macroValues.lastIndex)
    }

    LaunchedEffect(Unit) {
        viewModel.resetManualEntry()
    }

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
            KalaiCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = AppTheme.colors.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
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
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = s.common.calories,
                        color = AppTheme.colors.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = state.calories.toString(),
                        color = AppTheme.colors.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "kcal",
                        color = AppTheme.colors.onBackgroundSecondary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(26.dp))
                }
            }

            // Macro editors
            KalaiCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = AppTheme.colors.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    NutrientEditRow(
                        label = s.common.protein,
                        value = state.protein,
                        valueUnit = "g",
                        icon = ImageVector.vectorResource(R.drawable.chicken_leg),
                        activeColor = colorResource(id = R.color.proteinColor),
                        onClick = {
                            activePickerField =
                                if (activePickerField == ManualMacroPickerField.PROTEIN) null
                                else ManualMacroPickerField.PROTEIN
                        }
                    )
                    if (activePickerField == ManualMacroPickerField.PROTEIN) {
                        IosInlineValuePicker(
                            values = macroValues,
                            selectedIndex = selectedProteinIndex,
                            onIndexChanged = {
                                selectedProteinIndex = it
                                viewModel.onManualProteinChange(macroValues[it].toInt())
                            },
                            unitSuffix = "g",
                            itemHeight = 28.dp,
                            visibleItemsCount = 3,
                            textSize = 17.sp,
                            horizontalPadding = 8.dp,
                            bottomPadding = 2.dp
                        )
                    }
                    MacroDivider()

                    NutrientEditRow(
                        label = s.common.carbs,
                        value = state.carbs,
                        valueUnit = "g",
                        icon = ImageVector.vectorResource(R.drawable.wheat),
                        activeColor = colorResource(id = R.color.carbsColor),
                        onClick = {
                            activePickerField =
                                if (activePickerField == ManualMacroPickerField.CARBS) null
                                else ManualMacroPickerField.CARBS
                        }
                    )
                    if (activePickerField == ManualMacroPickerField.CARBS) {
                        IosInlineValuePicker(
                            values = macroValues,
                            selectedIndex = selectedCarbsIndex,
                            onIndexChanged = {
                                selectedCarbsIndex = it
                                viewModel.onManualCarbsChange(macroValues[it].toInt())
                            },
                            unitSuffix = "g",
                            itemHeight = 28.dp,
                            visibleItemsCount = 3,
                            textSize = 17.sp,
                            horizontalPadding = 8.dp,
                            bottomPadding = 2.dp
                        )
                    }
                    MacroDivider()

                    NutrientEditRow(
                        label = s.common.fat,
                        value = state.fat,
                        valueUnit = "g",
                        icon = ImageVector.vectorResource(R.drawable.avocado),
                        activeColor = colorResource(id = R.color.fatColor),
                        onClick = {
                            activePickerField =
                                if (activePickerField == ManualMacroPickerField.FAT) null
                                else ManualMacroPickerField.FAT
                        }
                    )
                    if (activePickerField == ManualMacroPickerField.FAT) {
                        IosInlineValuePicker(
                            values = macroValues,
                            selectedIndex = selectedFatIndex,
                            onIndexChanged = {
                                selectedFatIndex = it
                                viewModel.onManualFatChange(macroValues[it].toInt())
                            },
                            unitSuffix = "g",
                            itemHeight = 28.dp,
                            visibleItemsCount = 3,
                            textSize = 17.sp,
                            horizontalPadding = 8.dp,
                            bottomPadding = 2.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            KalaiButton(
                onClick = { viewModel.submitManualEntry() },
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
