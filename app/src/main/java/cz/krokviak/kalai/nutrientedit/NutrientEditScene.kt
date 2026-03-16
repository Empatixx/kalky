package cz.krokviak.kalai.nutrientedit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.R
import cz.krokviak.kalai.nutrientedit.components.NutrientEditRow
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.nutrientedit.components.VerticalCalorieCard
import cz.krokviak.kalai.settings.components.IosInlineValuePicker
import cz.krokviak.kalai.ui.components.KalaiCard
import cz.krokviak.kalai.i18n.LocalStrings
import cz.krokviak.kalai.ui.LocalDimensions
import cz.krokviak.kalai.ui.components.KalaiGradientBackground

private enum class MacroPickerField { PROTEIN, CARBS, FAT }

@Composable
fun NutrientEditScene(
    onBackClick: () -> Unit,
    nutrientEditViewModel: NutrientEditViewModel,
    uiState: NutrientEditState
) {
    val macroValues = remember { (0..500).map { it.toString() } }
    var activePickerField by remember { mutableStateOf<MacroPickerField?>(null) }
    var selectedProteinIndex by remember { mutableIntStateOf(resolveMacroIndex(uiState.protein, macroValues.lastIndex)) }
    var selectedCarbsIndex by remember { mutableIntStateOf(resolveMacroIndex(uiState.carbs, macroValues.lastIndex)) }
    var selectedFatIndex by remember { mutableIntStateOf(resolveMacroIndex(uiState.fat, macroValues.lastIndex)) }

    val dims = LocalDimensions.current
    KalaiGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dims.screenPadding, vertical = dims.itemSpacing),
            verticalArrangement = Arrangement.spacedBy(dims.itemSpacing)
        ) {
            NutrientEditTopBar(
                onBackClick = onBackClick,
                title = LocalStrings.current.nutrientEdit.title
            )
            VerticalCalorieCard(
                currentCalories = uiState.calories,
                calorieRatio = 0.5f,
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = LocalStrings.current.nutrientEdit.macronutrients,
                    color = AppTheme.colors.onBackgroundSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 12.dp)
                )
                KalaiCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppTheme.colors.border, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    color = AppTheme.colors.surface
                ) {
                    Column {
                        NutrientEditRow(
                            label = LocalStrings.current.common.protein,
                            value = uiState.protein,
                            valueUnit = "g",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (activePickerField == MacroPickerField.PROTEIN) {
                                    activePickerField = null
                                } else {
                                    selectedProteinIndex = resolveMacroIndex(uiState.protein, macroValues.lastIndex)
                                    activePickerField = MacroPickerField.PROTEIN
                                }
                            },
                            icon = ImageVector.vectorResource(R.drawable.chicken_leg),
                            activeColor = colorResource(id = R.color.proteinColor)
                        )
                        if (activePickerField == MacroPickerField.PROTEIN) {
                            IosInlineValuePicker(
                                values = macroValues,
                                selectedIndex = selectedProteinIndex,
                                onIndexChanged = {
                                    selectedProteinIndex = it
                                    nutrientEditViewModel.onProteinChange(macroValues[it].toInt())
                                },
                                unitSuffix = "g"
                            )
                        }
                        GroupDivider()
                        NutrientEditRow(
                            label = LocalStrings.current.common.carbs,
                            value = uiState.carbs,
                            valueUnit = "g",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (activePickerField == MacroPickerField.CARBS) {
                                    activePickerField = null
                                } else {
                                    selectedCarbsIndex = resolveMacroIndex(uiState.carbs, macroValues.lastIndex)
                                    activePickerField = MacroPickerField.CARBS
                                }
                            },
                            icon = ImageVector.vectorResource(R.drawable.wheat),
                            activeColor = colorResource(id = R.color.carbsColor)
                        )
                        if (activePickerField == MacroPickerField.CARBS) {
                            IosInlineValuePicker(
                                values = macroValues,
                                selectedIndex = selectedCarbsIndex,
                                onIndexChanged = {
                                    selectedCarbsIndex = it
                                    nutrientEditViewModel.onCarbsChange(macroValues[it].toInt())
                                },
                                unitSuffix = "g"
                            )
                        }
                        GroupDivider()
                        NutrientEditRow(
                            label = LocalStrings.current.common.fat,
                            value = uiState.fat,
                            valueUnit = "g",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (activePickerField == MacroPickerField.FAT) {
                                    activePickerField = null
                                } else {
                                    selectedFatIndex = resolveMacroIndex(uiState.fat, macroValues.lastIndex)
                                    activePickerField = MacroPickerField.FAT
                                }
                            },
                            icon = ImageVector.vectorResource(R.drawable.avocado),
                            activeColor = colorResource(id = R.color.fatColor)
                        )
                        if (activePickerField == MacroPickerField.FAT) {
                            IosInlineValuePicker(
                                values = macroValues,
                                selectedIndex = selectedFatIndex,
                                onIndexChanged = {
                                    selectedFatIndex = it
                                    nutrientEditViewModel.onFatChange(macroValues[it].toInt())
                                },
                                unitSuffix = "g"
                            )
                        }
                    }
                }
            }
        }        
    }
}

private fun resolveMacroIndex(value: Int, maxIndex: Int): Int = value.coerceIn(0, maxIndex)

@Composable
fun NutrientEditTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    title: String = ""
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable(onClick = onBackClick)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go Back",
                tint = AppTheme.colors.onBackground
            )
            Text(
                text = LocalStrings.current.common.back,
                color = AppTheme.colors.onBackground,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = title,
            color = AppTheme.colors.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 48.dp)
        )
    }
}

@Composable
private fun GroupDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(start = 56.dp, end = 14.dp)
            .background(AppTheme.colors.border)
    )
}
