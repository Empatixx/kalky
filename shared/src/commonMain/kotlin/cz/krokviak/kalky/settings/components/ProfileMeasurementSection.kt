package cz.krokviak.kalky.settings.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.common.UnitSystem
import cz.krokviak.kalky.i18n.LocalStrings
import cz.krokviak.kalky.settings.ProfilePickerField
import cz.krokviak.kalky.settings.SettingsUiState
import cz.krokviak.kalky.settings.SettingsViewModel
import cz.krokviak.kalky.settings.formatHeightForDisplay
import cz.krokviak.kalky.settings.formatWeightForDisplay
import cz.krokviak.kalky.settings.heightUnitLabel
import cz.krokviak.kalky.settings.weightUnitLabel
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.ui.components.KalkyCard

@Composable
fun ProfileMeasurementSection(
    uiState: SettingsUiState,
    viewModel: SettingsViewModel,
    unitSystem: UnitSystem,
    metricWeightValues: List<String>,
    metricHeightValues: List<String>,
    ageValues: List<String>,
    displayWeightValues: List<String>,
    displayHeightValues: List<String>,
    textSize: TextUnit = 20.sp,
) {
    val s = LocalStrings.current
    KalkyCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = AppTheme.colors.surface
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ProfileMeasurementRow(
                label = s.profile.weight,
                value = uiState.weight.toFloatOrNull()?.let { formatWeightForDisplay(it, unitSystem) } ?: "--.-",
                unit = weightUnitLabel(unitSystem),
                expanded = uiState.activePickerField == ProfilePickerField.WEIGHT,
                values = displayWeightValues,
                selectedIndex = resolveWeightIndex(uiState.weight),
                unitSuffix = weightUnitLabel(unitSystem),
                onClick = { viewModel.togglePickerField(ProfilePickerField.WEIGHT) },
                onIndexChanged = { viewModel.onWeightChange(metricWeightValues[it]) },
                textSize = textSize,
            )
            RowDivider()
            ProfileMeasurementRow(
                label = s.profile.height,
                value = uiState.height.toFloatOrNull()?.let { formatHeightForDisplay(it, unitSystem) } ?: "--",
                unit = heightUnitLabel(unitSystem),
                expanded = uiState.activePickerField == ProfilePickerField.HEIGHT,
                values = displayHeightValues,
                selectedIndex = resolveIndex(uiState.height, 100, 250),
                unitSuffix = heightUnitLabel(unitSystem),
                onClick = { viewModel.togglePickerField(ProfilePickerField.HEIGHT) },
                onIndexChanged = { viewModel.onHeightChange(metricHeightValues[it]) },
                textSize = textSize,
            )
            RowDivider()
            ProfileMeasurementRow(
                label = s.profile.age,
                value = uiState.age.ifBlank { "--" },
                unit = s.common.years,
                expanded = uiState.activePickerField == ProfilePickerField.AGE,
                values = ageValues,
                selectedIndex = resolveIndex(uiState.age, 1, 120),
                unitSuffix = s.common.years,
                onClick = { viewModel.togglePickerField(ProfilePickerField.AGE) },
                onIndexChanged = { viewModel.onAgeChange(ageValues[it]) },
                textSize = textSize,
            )
        }
    }
}

@Composable
private fun ProfileMeasurementRow(
    label: String,
    value: String,
    unit: String,
    expanded: Boolean,
    values: List<String>,
    selectedIndex: Int,
    unitSuffix: String,
    onClick: () -> Unit,
    onIndexChanged: (Int) -> Unit,
    textSize: TextUnit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
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
        if (expanded) {
            IosInlineValuePicker(
                values = values,
                selectedIndex = selectedIndex,
                onIndexChanged = onIndexChanged,
                unitSuffix = unitSuffix
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

internal fun resolveWeightIndex(value: String): Int {
    val parsed = value.toFloatOrNull() ?: return 509
    return ((parsed * 10f).toInt() - 300).coerceIn(0, 2200)
}

internal fun resolveIndex(value: String, minValue: Int, maxValue: Int): Int {
    val parsed = value.toIntOrNull() ?: return (maxValue - minValue) / 2
    return (parsed - minValue).coerceIn(0, maxValue - minValue)
}
