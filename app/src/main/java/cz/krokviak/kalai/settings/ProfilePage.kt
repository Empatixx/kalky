package cz.krokviak.kalai.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.settings.components.BmiIndicatorCard
import cz.krokviak.kalai.settings.components.IosInlineValuePicker
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.ui.components.KalaiCard
import cz.krokviak.kalai.i18n.LocalStrings
import cz.krokviak.kalai.ui.components.KalaiSegmentedControl
import java.util.Locale

private enum class ProfilePickerField { WEIGHT, HEIGHT, AGE }

@Composable
fun ProfilePage(
    uiState: SettingsUiState,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current
    val genderOptions = listOf(s.profile.male, s.profile.female)
    val genderKeys = listOf("Mu\u017E", "\u017Dena")
    val activityLabels = listOf(s.profile.sedentary, s.profile.light, s.profile.active, s.profile.veryActive)
    val cardContentInset = 12.dp
    val cardTextSize = 20.sp
    val unitSystem by AppPreferencesManager.unitSystem.collectAsState()

    val metricWeightValues = remember {
        (300..2500).map { index -> String.format(Locale.US, "%.1f", index / 10f) }
    }
    val metricHeightValues = remember { (100..250).map { it.toString() } }
    val ageValues = remember { (1..120).map { it.toString() } }
    val displayWeightValues = remember(metricWeightValues, unitSystem) {
        if (unitSystem == UnitSystem.METRIC) {
            metricWeightValues
        } else {
            metricWeightValues.map { formatWeightForDisplay(it.toFloat(), unitSystem) }
        }
    }
    val displayHeightValues = remember(metricHeightValues, unitSystem) {
        if (unitSystem == UnitSystem.METRIC) {
            metricHeightValues
        } else {
            metricHeightValues.map { formatHeightForDisplay(it.toFloat(), unitSystem) }
        }
    }

    var activePickerField by remember { mutableStateOf<ProfilePickerField?>(null) }
    var selectedWeightIndex by remember { mutableIntStateOf(resolveWeightIndex(uiState.weight)) }
    var selectedHeightIndex by remember { mutableIntStateOf(resolveIndex(uiState.height, 100, 250)) }
    var selectedAgeIndex by remember { mutableIntStateOf(resolveIndex(uiState.age, 1, 120)) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = s.profile.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = AppTheme.colors.onBackground
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionHeader(
                title = s.profile.personalInfo,
                startInset = cardContentInset
            )
            KalaiCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = AppTheme.colors.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ProfileInfoRow(
                        label = s.profile.weight,
                        value = formatWeightValue(uiState.weight, unitSystem),
                        unit = weightUnitLabel(unitSystem),
                        onClick = {
                            if (activePickerField == ProfilePickerField.WEIGHT) {
                                activePickerField = null
                            } else {
                                selectedWeightIndex = resolveWeightIndex(uiState.weight)
                                activePickerField = ProfilePickerField.WEIGHT
                            }
                        },
                        textSize = cardTextSize
                    )
                    if (activePickerField == ProfilePickerField.WEIGHT) {
                        IosInlineValuePicker(
                            values = displayWeightValues,
                            selectedIndex = selectedWeightIndex,
                            onIndexChanged = {
                                selectedWeightIndex = it
                                viewModel.onWeightChange(metricWeightValues[it])
                            },
                            unitSuffix = weightUnitLabel(unitSystem)
                        )
                    }
                    RowDivider()

                    ProfileInfoRow(
                        label = s.profile.height,
                        value = formatHeightValue(uiState.height, unitSystem),
                        unit = heightUnitLabel(unitSystem),
                        onClick = {
                            if (activePickerField == ProfilePickerField.HEIGHT) {
                                activePickerField = null
                            } else {
                                selectedHeightIndex = resolveIndex(uiState.height, 100, 250)
                                activePickerField = ProfilePickerField.HEIGHT
                            }
                        },
                        textSize = cardTextSize
                    )
                    if (activePickerField == ProfilePickerField.HEIGHT) {
                        IosInlineValuePicker(
                            values = displayHeightValues,
                            selectedIndex = selectedHeightIndex,
                            onIndexChanged = {
                                selectedHeightIndex = it
                                viewModel.onHeightChange(metricHeightValues[it])
                            },
                            unitSuffix = heightUnitLabel(unitSystem)
                        )
                    }
                    RowDivider()

                    ProfileInfoRow(
                        label = s.profile.age,
                        value = uiState.age.ifBlank { "--" },
                        unit = s.common.years,
                        onClick = {
                            if (activePickerField == ProfilePickerField.AGE) {
                                activePickerField = null
                            } else {
                                selectedAgeIndex = resolveIndex(uiState.age, 1, 120)
                                activePickerField = ProfilePickerField.AGE
                            }
                        },
                        textSize = cardTextSize
                    )
                    if (activePickerField == ProfilePickerField.AGE) {
                        IosInlineValuePicker(
                            values = ageValues,
                            selectedIndex = selectedAgeIndex,
                            onIndexChanged = {
                                selectedAgeIndex = it
                                viewModel.onAgeChange(ageValues[it])
                            },
                            unitSuffix = s.common.years
                        )
                    }
                }
            }
        }

        SectionHeader(
            title = s.profile.gender,
            startInset = cardContentInset
        )
        KalaiSegmentedControl(
            selectedIndex = genderKeys.indexOf(uiState.gender).coerceAtLeast(0),
            items = genderOptions,
            onItemSelected = { viewModel.onGenderChange(genderKeys[it]) },
            modifier = Modifier.fillMaxWidth(),
            trackColor = AppTheme.colors.surfaceSecondary,
            indicatorColor = AppTheme.colors.surface,
            textSize = cardTextSize
        )

        uiState.bmi?.let { bmi ->
            BmiIndicatorCard(
                bmi = bmi,
                textSize = cardTextSize,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionHeader(
                title = s.profile.activityLevel,
                startInset = cardContentInset
            )
            KalaiCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = AppTheme.colors.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    activityLabels.forEachIndexed { index, label ->
                        val level = index + 1
                        ActivityLevelRow(
                            label = label,
                            selected = uiState.activityLevel == level,
                            onClick = { viewModel.onActivityLevelChange(level) },
                            textSize = cardTextSize
                        )
                        if (index < activityLabels.lastIndex) {
                            RowDivider()
                        }
                    }
                }
            }
        }
    } 
}

private fun resolveWeightIndex(value: String): Int {
    val parsed = value.toFloatOrNull() ?: return 509 // 80.9
    return ((parsed * 10f).toInt() - 300).coerceIn(0, 2200)
}

private fun resolveIndex(value: String, minValue: Int, maxValue: Int): Int {
    val parsed = value.toIntOrNull() ?: return (maxValue - minValue) / 2
    return (parsed - minValue).coerceIn(0, maxValue - minValue)
}

private fun formatWeightValue(metricWeightValue: String, unitSystem: UnitSystem): String {
    val metricWeight = metricWeightValue.toFloatOrNull() ?: return "--.-"
    return formatWeightForDisplay(metricWeight, unitSystem)
}

private fun formatHeightValue(metricHeightValue: String, unitSystem: UnitSystem): String {
    val metricHeight = metricHeightValue.toFloatOrNull() ?: return "--"
    return formatHeightForDisplay(metricHeight, unitSystem)
}

@Composable
private fun SectionHeader(
    title: String,
    startInset: Dp
) {
    Text(
        text = title,
        color = AppTheme.colors.onBackgroundSecondary,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = startInset)
    )
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String,
    unit: String,
    onClick: () -> Unit,
    textSize: TextUnit = 20.sp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = AppTheme.colors.onBackground,
            fontSize = textSize,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = AppTheme.colors.onBackground,
            fontSize = textSize,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = unit,
            color = AppTheme.colors.onBackgroundSecondary,
            fontSize = textSize
        )
        Spacer(modifier = Modifier.width(2.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = AppTheme.colors.onBackgroundSecondary
        )
    }
}

@Composable
private fun ActivityLevelRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    textSize: TextUnit = 20.sp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = AppTheme.colors.onBackground,
            fontSize = textSize,
            fontWeight = FontWeight.Normal
        )
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Composable
private fun RowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 12.dp)
            .background(AppTheme.colors.border)
    )
}
