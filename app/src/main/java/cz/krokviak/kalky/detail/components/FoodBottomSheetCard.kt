package cz.krokviak.kalky.detail.components

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
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.R
import cz.krokviak.kalky.nutrientedit.components.NutrientEditRow
import cz.krokviak.kalky.settings.components.IosInlineValuePicker
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.i18n.LocalStrings
import cz.krokviak.kalky.ui.LocalDimensions
import cz.krokviak.kalky.ui.components.KalkyCard

private enum class DetailMacroPickerField { PROTEIN, CARBS, FAT }

@Composable
fun BoxScope.FoodBottomSheetCard(
    name: String,
    calories: Int,
    protein: Int,
    fats: Int,
    carbs: Int,
    modifier: Modifier = Modifier,
    onFixResult: () -> Unit,
    onFinish: () -> Unit,
    onProteinChange: (Int) -> Unit,
    onCarbsChange: (Int) -> Unit,
    onFatChange: (Int) -> Unit
) {
    val macroValues = remember { (0..500).map { it.toString() } }
    val sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
    var activePickerField by remember { mutableStateOf<DetailMacroPickerField?>(null) }
    var selectedProteinIndex by remember { mutableIntStateOf(resolveMacroIndex(protein, macroValues.lastIndex)) }
    var selectedCarbsIndex by remember { mutableIntStateOf(resolveMacroIndex(carbs, macroValues.lastIndex)) }
    var selectedFatIndex by remember { mutableIntStateOf(resolveMacroIndex(fats, macroValues.lastIndex)) }

    LaunchedEffect(protein) {
        selectedProteinIndex = resolveMacroIndex(protein, macroValues.lastIndex)
    }
    LaunchedEffect(carbs) {
        selectedCarbsIndex = resolveMacroIndex(carbs, macroValues.lastIndex)
    }
    LaunchedEffect(fats) {
        selectedFatIndex = resolveMacroIndex(fats, macroValues.lastIndex)
    }

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
                    shape = RoundedCornerShape(16.dp),
                    color = AppTheme.colors.surface
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val s = LocalStrings.current
                        NutrientEditRow(
                            label = s.common.protein,
                            value = protein,
                            valueUnit = "g",
                            icon = ImageVector.vectorResource(R.drawable.chicken_leg),
                            activeColor = colorResource(id = R.color.proteinColor),
                            onClick = {
                                activePickerField =
                                    if (activePickerField == DetailMacroPickerField.PROTEIN) null
                                    else DetailMacroPickerField.PROTEIN
                            }
                        )
                        if (activePickerField == DetailMacroPickerField.PROTEIN) {
                            IosInlineValuePicker(
                                values = macroValues,
                                selectedIndex = selectedProteinIndex,
                                onIndexChanged = {
                                    selectedProteinIndex = it
                                    onProteinChange(macroValues[it].toInt())
                                },
                                unitSuffix = "g",
                                itemHeight = 28.dp,
                                visibleItemsCount = 3,
                                textSize = 17.sp,
                                horizontalPadding = 8.dp,
                                bottomPadding = 2.dp
                            )
                        }
                        DetailGroupDivider()

                        NutrientEditRow(
                            label = s.common.carbs,
                            value = carbs,
                            valueUnit = "g",
                            icon = ImageVector.vectorResource(R.drawable.wheat),
                            activeColor = colorResource(id = R.color.carbsColor),
                            onClick = {
                                activePickerField =
                                    if (activePickerField == DetailMacroPickerField.CARBS) null
                                    else DetailMacroPickerField.CARBS
                            }
                        )
                        if (activePickerField == DetailMacroPickerField.CARBS) {
                            IosInlineValuePicker(
                                values = macroValues,
                                selectedIndex = selectedCarbsIndex,
                                onIndexChanged = {
                                    selectedCarbsIndex = it
                                    onCarbsChange(macroValues[it].toInt())
                                },
                                unitSuffix = "g",
                                itemHeight = 28.dp,
                                visibleItemsCount = 3,
                                textSize = 17.sp,
                                horizontalPadding = 8.dp,
                                bottomPadding = 2.dp
                            )
                        }
                        DetailGroupDivider()

                        NutrientEditRow(
                            label = s.common.fat,
                            value = fats,
                            valueUnit = "g",
                            icon = ImageVector.vectorResource(R.drawable.avocado),
                            activeColor = colorResource(id = R.color.fatColor),
                            onClick = {
                                activePickerField =
                                    if (activePickerField == DetailMacroPickerField.FAT) null
                                    else DetailMacroPickerField.FAT
                            }
                        )
                        if (activePickerField == DetailMacroPickerField.FAT) {
                            IosInlineValuePicker(
                                values = macroValues,
                                selectedIndex = selectedFatIndex,
                                onIndexChanged = {
                                    selectedFatIndex = it
                                    onFatChange(macroValues[it].toInt())
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
            }

            FoodDetailButtons(
                onFixResult = onFixResult,
                onFinish = onFinish,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun resolveMacroIndex(value: Int, maxIndex: Int): Int = value.coerceIn(0, maxIndex)

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
                    contentDescription = "Calories",
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
                text = "kcal",
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
