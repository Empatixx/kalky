package cz.krokviak.kalky.scenes.onboarding.pages

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import cz.krokviak.kalky.core.common.utils.caloriesFromMacros
import cz.krokviak.kalky.core.theme.MacroColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.krokviak.kalky.scenes.nutrientedit.MacroField
import cz.krokviak.kalky.scenes.nutrientedit.components.VerticalCalorieCard
import cz.krokviak.kalky.scenes.onboarding.GoalChoice
import cz.krokviak.kalky.core.common.AppLanguage
import cz.krokviak.kalky.core.common.UnitSystem
import cz.krokviak.kalky.core.common.entities.Gender
import cz.krokviak.kalky.scenes.settings.components.IosInlineValuePicker
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.core.ui.components.MacroPickerRow
import cz.krokviak.kalky.core.theme.ThemeMode
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.core.ui.LocalDimensions
import cz.krokviak.kalky.core.ui.components.KalkyCard

@Composable
fun LanguageOnboardingPage(
    selectedLanguage: AppLanguage,
    onSelected: (AppLanguage) -> Unit
) {
    val s = LocalStrings.current
    val languages = AppLanguage.entries
    ChoiceOnboardingPage(
        title = s.onboarding.chooseLanguage,
        options = languages.map { it.displayName },
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
    selectedGender: Gender,
    onSelected: (Gender) -> Unit
) {
    val s = LocalStrings.current
    val genders = Gender.entries
    ChoiceOnboardingPage(
        title = s.onboarding.chooseGender,
        options = listOf(s.profile.male, s.profile.female),
        selectedIndex = genders.indexOf(selectedGender).coerceAtLeast(0),
        onSelected = { onSelected(genders[it]) }
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
    var expandedField by remember { mutableStateOf<MacroField?>(null) }
    val calories = caloriesFromMacros(protein, carbs, fat)

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
                .border(1.dp, AppTheme.colors.border, RoundedCornerShape(LocalDimensions.current.cardCornerRadius)),
            shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
            color = AppTheme.colors.surface
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                MacroPickerRow(
                    label = s.common.protein,
                    value = protein,
                    icon = Icons.Default.Restaurant,
                    activeColor = MacroColors.protein,
                    expanded = expandedField == MacroField.PROTEIN,
                    onClick = {
                        expandedField = if (expandedField == MacroField.PROTEIN) null else MacroField.PROTEIN
                    },
                    onValueChange = onProteinChanged
                )
                OnboardingGroupDivider()
                MacroPickerRow(
                    label = s.common.carbs,
                    value = carbs,
                    icon = Icons.Default.Spa,
                    activeColor = MacroColors.carbs,
                    expanded = expandedField == MacroField.CARBS,
                    onClick = {
                        expandedField = if (expandedField == MacroField.CARBS) null else MacroField.CARBS
                    },
                    onValueChange = onCarbsChanged
                )
                OnboardingGroupDivider()
                MacroPickerRow(
                    label = s.common.fat,
                    value = fat,
                    icon = Icons.Default.Eco,
                    activeColor = MacroColors.fat,
                    expanded = expandedField == MacroField.FAT,
                    onClick = {
                        expandedField = if (expandedField == MacroField.FAT) null else MacroField.FAT
                    },
                    onValueChange = onFatChanged
                )
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
