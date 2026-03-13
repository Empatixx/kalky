package cz.krokviak.kalai.onboarding.pages

import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.onboarding.GoalChoice
import cz.krokviak.kalai.settings.AppLanguage
import cz.krokviak.kalai.settings.AppPreferencesManager
import cz.krokviak.kalai.settings.UnitSystem
import cz.krokviak.kalai.settings.components.IosInlineValuePicker
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.theme.ThemeManager
import cz.krokviak.kalai.theme.ThemeMode
import cz.krokviak.kalai.i18n.LocalStrings
import cz.krokviak.kalai.ui.components.KalaiCard

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
    val calories = protein * 4 + carbs * 4 + fat * 9

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = s.onboarding.yourDailyTargets,
            color = AppTheme.colors.onBackground,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = s.onboarding.macrosIndicativeNote,
            color = AppTheme.colors.onBackgroundSecondary,
            fontSize = 14.sp
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$calories",
                color = AppTheme.colors.onBackground,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "kcal",
                color = AppTheme.colors.onBackgroundSecondary,
                fontSize = 16.sp
            )
        }
        KalaiCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = AppTheme.colors.surface
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                MacroPickerRow(
                    label = s.common.protein,
                    value = protein,
                    unitSuffix = "g",
                    expanded = expandedField == MacroField.PROTEIN,
                    onToggle = { expandedField = if (expandedField == MacroField.PROTEIN) null else MacroField.PROTEIN },
                    values = macroValues,
                    onValueChanged = onProteinChanged
                )
                PickerDivider()
                MacroPickerRow(
                    label = s.common.carbs,
                    value = carbs,
                    unitSuffix = "g",
                    expanded = expandedField == MacroField.CARBS,
                    onToggle = { expandedField = if (expandedField == MacroField.CARBS) null else MacroField.CARBS },
                    values = macroValues,
                    onValueChanged = onCarbsChanged
                )
                PickerDivider()
                MacroPickerRow(
                    label = s.common.fat,
                    value = fat,
                    unitSuffix = "g",
                    expanded = expandedField == MacroField.FAT,
                    onToggle = { expandedField = if (expandedField == MacroField.FAT) null else MacroField.FAT },
                    values = macroValues,
                    onValueChanged = onFatChanged
                )
            }
        }
    }
}

@Composable
private fun MacroPickerRow(
    label: String,
    value: Int,
    unitSuffix: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    values: List<String>,
    onValueChanged: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = AppTheme.colors.onBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$value $unitSuffix",
                color = if (expanded) AppTheme.colors.onBackground else AppTheme.colors.onBackgroundSecondary,
                fontSize = 16.sp,
                fontWeight = if (expanded) FontWeight.SemiBold else FontWeight.Normal
            )
        }
        if (expanded) {
            IosInlineValuePicker(
                values = values,
                selectedIndex = value.coerceIn(0, values.lastIndex),
                onIndexChanged = { onValueChanged(values[it].toInt()) },
                unitSuffix = unitSuffix
            )
        }
    }
}

@Composable
private fun PickerDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 14.dp)
            .background(AppTheme.colors.border)
    )
}

@Composable
fun PromoCodeOnboardingPage(
    promoCode: String,
    onPromoCodeChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = LocalStrings.current.onboarding.havePromoCode,
            color = AppTheme.colors.onBackground,
            fontSize = 28.sp,
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
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            color = AppTheme.colors.onBackground,
            fontSize = 28.sp,
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
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            color = AppTheme.colors.onBackground,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )
        KalaiCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = AppTheme.colors.surface
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                options.forEachIndexed { index, label ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clickable { onSelected(index) }
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            color = AppTheme.colors.onBackground,
                            fontSize = 20.sp,
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
