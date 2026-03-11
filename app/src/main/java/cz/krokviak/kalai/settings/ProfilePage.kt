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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import cz.krokviak.kalai.ui.components.KalaiButton
import cz.krokviak.kalai.ui.components.KalaiCard
import cz.krokviak.kalai.ui.components.KalaiSegmentedControl
import java.util.Locale

private enum class ProfilePickerField { WEIGHT, HEIGHT, AGE }

@Composable
fun ProfilePage(
    uiState: SettingsUiState,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val genderOptions = listOf("Muž", "Žena")
    val activityLabels = listOf("Sedavý", "Mírný", "Aktivní", "Velmi aktivní")
    val cardContentInset = 12.dp
    val cardTextSize = 20.sp

    val weightValues = remember {
        (300..2500).map { index -> String.format(Locale.US, "%.1f", index / 10f) }
    }
    val heightValues = remember { (100..250).map { it.toString() } }
    val ageValues = remember { (1..120).map { it.toString() } }

    var activePickerField by remember { mutableStateOf<ProfilePickerField?>(null) }
    var selectedWeightIndex by remember { mutableIntStateOf(resolveWeightIndex(uiState.weight)) }
    var selectedHeightIndex by remember { mutableIntStateOf(resolveIndex(uiState.height, 100, 250)) }
    var selectedAgeIndex by remember { mutableIntStateOf(resolveIndex(uiState.age, 1, 120)) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profil",
                color = AppTheme.colors.onBackground,
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = if (uiState.saved) "Uloženo" else "Uložit",
                color = Color(0xFF4A82E8),
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { viewModel.save() }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionHeader(
                title = "Osobní údaje",
                startInset = cardContentInset,
                emphasized = true
            )
            KalaiCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = AppTheme.colors.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ProfileInfoRow(
                        label = "Váha",
                        value = uiState.weight.ifBlank { "--.-" },
                        unit = "kg",
                        onClick = {
                            selectedWeightIndex = resolveWeightIndex(uiState.weight)
                            activePickerField = ProfilePickerField.WEIGHT
                        },
                        textSize = cardTextSize
                    )
                    if (activePickerField == ProfilePickerField.WEIGHT) {
                        IosInlineValuePicker(
                            values = weightValues,
                            selectedIndex = selectedWeightIndex,
                            onIndexChanged = { selectedWeightIndex = it },
                            onCancel = { activePickerField = null },
                            onDone = {
                                viewModel.onWeightChange(weightValues[selectedWeightIndex])
                                activePickerField = null
                            }
                        )
                    }
                    RowDivider()

                    ProfileInfoRow(
                        label = "Výška",
                        value = uiState.height.ifBlank { "--" },
                        unit = "cm",
                        onClick = {
                            selectedHeightIndex = resolveIndex(uiState.height, 100, 250)
                            activePickerField = ProfilePickerField.HEIGHT
                        },
                        textSize = cardTextSize
                    )
                    if (activePickerField == ProfilePickerField.HEIGHT) {
                        IosInlineValuePicker(
                            values = heightValues,
                            selectedIndex = selectedHeightIndex,
                            onIndexChanged = { selectedHeightIndex = it },
                            onCancel = { activePickerField = null },
                            onDone = {
                                viewModel.onHeightChange(heightValues[selectedHeightIndex])
                                activePickerField = null
                            }
                        )
                    }
                    RowDivider()

                    ProfileInfoRow(
                        label = "Věk",
                        value = uiState.age.ifBlank { "--" },
                        unit = "let",
                        onClick = {
                            selectedAgeIndex = resolveIndex(uiState.age, 1, 120)
                            activePickerField = ProfilePickerField.AGE
                        },
                        textSize = cardTextSize
                    )
                    if (activePickerField == ProfilePickerField.AGE) {
                        IosInlineValuePicker(
                            values = ageValues,
                            selectedIndex = selectedAgeIndex,
                            onIndexChanged = { selectedAgeIndex = it },
                            onCancel = { activePickerField = null },
                            onDone = {
                                viewModel.onAgeChange(ageValues[selectedAgeIndex])
                                activePickerField = null
                            }
                        )
                    }
                }
            }
        }

        SectionHeader(
            title = "Pohlaví",
            startInset = cardContentInset,
            emphasized = true
        )
        KalaiCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = AppTheme.colors.surface
        ) {
            KalaiSegmentedControl(
                selectedIndex = genderOptions.indexOf(uiState.gender).coerceAtLeast(0),
                items = genderOptions,
                onItemSelected = { viewModel.onGenderChange(genderOptions[it]) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                trackColor = AppTheme.colors.surfaceSecondary,
                indicatorColor = AppTheme.colors.surface,
                textSize = cardTextSize
            )
        }

        uiState.bmi?.let { bmi ->
            BmiIndicatorCard(
                bmi = bmi,
                textSize = cardTextSize,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionHeader(
                title = "Úroveň aktivity",
                startInset = cardContentInset,
                emphasized = true
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

        KalaiButton(
            onClick = { viewModel.save() },
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Black,
            contentColor = Color.White
        ) {
            Text(
                text = "Uložit údaje",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
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

@Composable
private fun SectionHeader(
    title: String,
    startInset: Dp,
    emphasized: Boolean = false
) {
    Text(
        text = title,
        color = AppTheme.colors.onBackground,
        fontSize = if (emphasized) 20.sp else 16.sp,
        fontWeight = if (emphasized) FontWeight.Bold else FontWeight.SemiBold,
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
