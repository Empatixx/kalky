package cz.krokviak.kalky.onboarding.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.ui.graphics.vector.ImageVector
import cz.krokviak.kalky.theme.MacroColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.nutrientedit.components.NutrientEditRow
import cz.krokviak.kalky.nutrientedit.components.VerticalCalorieCard
import cz.krokviak.kalky.onboarding.GoalChoice
import cz.krokviak.kalky.common.AppLanguage
import cz.krokviak.kalky.common.UnitSystem
import cz.krokviak.kalky.settings.components.IosInlineValuePicker
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.theme.ThemeManager
import cz.krokviak.kalky.theme.ThemeMode
import cz.krokviak.kalky.i18n.LocalStrings
import cz.krokviak.kalky.ui.LocalDimensions
import cz.krokviak.kalky.ui.components.KalkyCard

@Composable
fun LanguageOnboardingPage(
    selectedLanguage: AppLanguage,
    onSelected: (AppLanguage) -> Unit
) {
    val s = LocalStrings.current
    val languages = AppLanguage.entries
    ChoiceOnboardingPage(
        title = s.onboarding.chooseLanguage,
        options = listOf("\u010Ce\u0161tina", "English"),
        selectedIndex = languages.indexOf(selectedLanguage).coerceAtLeast(0),
        onSelected = { onSelected(languages[it]) }
    )
}

@Composable
fun UnitsOnboardingPage(
    selectedUnit: UnitSystem,
    onSelected: (UnitSystem) -> Unit
) {
    val s = LocalStrings.current
    val units = UnitSystem.entries
    ChoiceOnboardingPage(
        title = s.onboarding.chooseUnits,
        options = listOf(s.settings.metric, s.settings.imperial),
        selectedIndex = units.indexOf(selectedUnit).coerceAtLeast(0),
        onSelected = { onSelected(units[it]) }
    )
}

@Composable
fun AppearanceOnboardingPage(
    selectedTheme: ThemeMode,
    onSelected: (ThemeMode) -> Unit
) {
    val s = LocalStrings.current
    val modes = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)
    ChoiceOnboardingPage(
        title = s.onboarding.chooseAppearance,
        options = listOf(s.settings.themeSystem, s.settings.themeLight, s.settings.themeDark),
        selectedIndex = modes.indexOf(selectedTheme).coerceAtLeast(0),
        onSelected = { onSelected(modes[it]) }
    )
}

@Composable
fun GenderOnboardingPage(
    selectedGender: String,
    onSelected: (String) -> Unit
) {
    val s = LocalStrings.current
    val genderKeys = listOf("Mu\u017E", "\u017Dena")
    ChoiceOnboardingPage(
        title = s.onboarding.chooseGender,
        options = listOf(s.profile.male, s.profile.female),
        selectedIndex = genderKeys.indexOf(selectedGender).coerceAtLeast(0),
        onSelected = { onSelected(genderKeys[it]) }
    )
}

@Composable
fun WeightOnboardingPage(
    values: List<String>,
    selectedIndex: Int,
    unitSuffix: String = "kg",
    onIndexChanged: (Int) -> Unit
) {
    PickerOnboardingPage(
        title = LocalStrings.current.onboarding.howMuchWeigh,
        values = values,
        selectedIndex = selectedIndex,
        unitSuffix = unitSuffix,
        onIndexChanged = onIndexChanged
    )
}

@Composable
fun HeightOnboardingPage(
    values: List<String>,
    selectedIndex: Int,
    unitSuffix: String = "cm",
    onIndexChanged: (Int) -> Unit
) {
    PickerOnboardingPage(
        title = LocalStrings.current.onboarding.howTall,
        values = values,
        selectedIndex = selectedIndex,
        unitSuffix = unitSuffix,
        onIndexChanged = onIndexChanged
    )
}

@Composable
fun AgeOnboardingPage(
    values: List<String>,
    selectedIndex: Int,
    onIndexChanged: (Int) -> Unit
) {
    PickerOnboardingPage(
        title = LocalStrings.current.onboarding.howOld,
        values = values,
        selectedIndex = selectedIndex,
        unitSuffix = LocalStrings.current.common.years,
        onIndexChanged = onIndexChanged
    )
}

@Composable
fun ActivityOnboardingPage(
    selectedActivityLevel: Int,
    onSelected: (Int) -> Unit
) {
    val s = LocalStrings.current
    ChoiceOnboardingPage(
        title = s.onboarding.howActive,
        options = listOf(s.profile.sedentary, s.profile.light, s.profile.active, s.profile.veryActive),
        selectedIndex = (selectedActivityLevel - 1).coerceIn(0, 3),
        onSelected = { onSelected(it + 1) }
    )
}

@Composable
fun GoalOnboardingPage(
    selectedGoal: GoalChoice,
    onSelected: (GoalChoice) -> Unit
) {
    val s = LocalStrings.current
    val goals = GoalChoice.entries
    val goalLabels = listOf(s.onboarding.loseWeight, s.onboarding.maintain, s.onboarding.gainWeight)
    ChoiceOnboardingPage(
        title = s.onboarding.whatsYourGoal,
        options = goalLabels,
        selectedIndex = goals.indexOf(selectedGoal).coerceAtLeast(0),
        onSelected = { onSelected(goals[it]) }
    )
}

private enum class MacroField { PROTEIN, CARBS, FAT }

@Composable
fun MacrosOnboardingPage(
    protein: Int,
    carbs: Int,
    fat: Int,
    onProteinChanged: (Int) -> Unit,
    onCarbsChanged: (Int) -> Unit,
    onFatChanged: (Int) -> Unit
) {
    val s = LocalStrings.current
    val macroValues = remember { (0..500).map { it.toString() } }
    var expandedField by remember { mutableStateOf<MacroField?>(null) }
    var selectedProteinIndex by remember { mutableIntStateOf(protein.coerceIn(0, 500)) }
    var selectedCarbsIndex by remember { mutableIntStateOf(carbs.coerceIn(0, 500)) }
    var selectedFatIndex by remember { mutableIntStateOf(fat.coerceIn(0, 500)) }
    val calories = protein * 4 + carbs * 4 + fat * 9

    val dims = LocalDimensions.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = s.onboarding.yourDailyTargets,
            color = AppTheme.colors.onBackground,
            fontSize = dims.fontTitle,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = s.onboarding.macrosIndicativeNote,
            color = AppTheme.colors.onBackgroundSecondary,
            fontSize = dims.fontSmall
        )
        VerticalCalorieCard(
            currentCalories = calories,
            calorieRatio = 0.5f,
            modifier = Modifier.fillMaxWidth()
        )
        KalkyCard(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AppTheme.colors.border, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = AppTheme.colors.surface
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                NutrientEditRow(
                    label = s.common.protein,
                    value = protein,
                    valueUnit = "g",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (expandedField == MacroField.PROTEIN) {
                            expandedField = null
                        } else {
                            selectedProteinIndex = protein.coerceIn(0, macroValues.lastIndex)
                            expandedField = MacroField.PROTEIN
                        }
                    },
                    icon = Icons.Default.Restaurant,
                    activeColor = MacroColors.protein
                )
                if (expandedField == MacroField.PROTEIN) {
                    IosInlineValuePicker(
                        values = macroValues,
                        selectedIndex = selectedProteinIndex,
                        onIndexChanged = {
                            selectedProteinIndex = it
                            onProteinChanged(macroValues[it].toInt())
                        },
                        unitSuffix = "g"
                    )
                }
                OnboardingGroupDivider()
                NutrientEditRow(
                    label = s.common.carbs,
                    value = carbs,
                    valueUnit = "g",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (expandedField == MacroField.CARBS) {
                            expandedField = null
                        } else {
                            selectedCarbsIndex = carbs.coerceIn(0, macroValues.lastIndex)
                            expandedField = MacroField.CARBS
                        }
                    },
                    icon = Icons.Default.Spa,
                    activeColor = MacroColors.carbs
                )
                if (expandedField == MacroField.CARBS) {
                    IosInlineValuePicker(
                        values = macroValues,
                        selectedIndex = selectedCarbsIndex,
                        onIndexChanged = {
                            selectedCarbsIndex = it
                            onCarbsChanged(macroValues[it].toInt())
                        },
                        unitSuffix = "g"
                    )
                }
                OnboardingGroupDivider()
                NutrientEditRow(
                    label = s.common.fat,
                    value = fat,
                    valueUnit = "g",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (expandedField == MacroField.FAT) {
                            expandedField = null
                        } else {
                            selectedFatIndex = fat.coerceIn(0, macroValues.lastIndex)
                            expandedField = MacroField.FAT
                        }
                    },
                    icon = Icons.Default.Eco,
                    activeColor = MacroColors.fat
                )
                if (expandedField == MacroField.FAT) {
                    IosInlineValuePicker(
                        values = macroValues,
                        selectedIndex = selectedFatIndex,
                        onIndexChanged = {
                            selectedFatIndex = it
                            onFatChanged(macroValues[it].toInt())
                        },
                        unitSuffix = "g"
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingGroupDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(start = 56.dp, end = 14.dp)
            .background(AppTheme.colors.border)
    )
}

@Composable
fun PromoCodeOnboardingPage(
    promoCode: String,
    onPromoCodeChange: (String) -> Unit
) {
    val dims = LocalDimensions.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = LocalStrings.current.onboarding.havePromoCode,
            color = AppTheme.colors.onBackground,
            fontSize = dims.fontTitle,
            fontWeight = FontWeight.ExtraBold
        )
        OutlinedTextField(
            value = promoCode,
            onValueChange = onPromoCodeChange,
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = LocalStrings.current.onboarding.promoCodeOptional,
                    color = AppTheme.colors.onBackgroundSecondary
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = AppTheme.colors.surface,
                unfocusedContainerColor = AppTheme.colors.surface,
                disabledContainerColor = AppTheme.colors.surface,
                focusedTextColor = AppTheme.colors.onBackground,
                unfocusedTextColor = AppTheme.colors.onBackground,
                focusedIndicatorColor = AppTheme.colors.primary,
                unfocusedIndicatorColor = AppTheme.colors.border,
                cursorColor = AppTheme.colors.onBackground
            )
        )
    }
}

@Composable
private fun PickerOnboardingPage(
    title: String,
    values: List<String>,
    selectedIndex: Int,
    unitSuffix: String,
    onIndexChanged: (Int) -> Unit
) {
    val dims = LocalDimensions.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            color = AppTheme.colors.onBackground,
            fontSize = dims.fontTitle,
            fontWeight = FontWeight.ExtraBold
        )
        IosInlineValuePicker(
            values = values,
            selectedIndex = selectedIndex,
            onIndexChanged = onIndexChanged,
            unitSuffix = unitSuffix
        )
    }
}

@Composable
private fun ChoiceOnboardingPage(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    val dims = LocalDimensions.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            color = AppTheme.colors.onBackground,
            fontSize = dims.fontTitle,
            fontWeight = FontWeight.ExtraBold
        )
        KalkyCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = AppTheme.colors.surface
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                options.forEachIndexed { index, label ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dims.rowHeight)
                            .clickable { onSelected(index) }
                            .padding(horizontal = dims.cardPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            color = AppTheme.colors.onBackground,
                            fontSize = dims.fontSubtitle,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        if (selectedIndex == index) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    }
                    if (index < options.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(horizontal = 14.dp)
                                .background(AppTheme.colors.border)
                        )
                    }
                }
            }
        }
    }
}
