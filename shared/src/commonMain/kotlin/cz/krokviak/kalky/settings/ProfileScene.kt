package cz.krokviak.kalky.settings

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.settings.components.BmiIndicatorCard
import cz.krokviak.kalky.settings.components.ProfileMeasurementSection
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.ui.components.KalkyCard
import cz.krokviak.kalky.common.AppPreferences
import cz.krokviak.kalky.common.UnitSystem
import cz.krokviak.kalky.di.koinInject
import cz.krokviak.kalky.i18n.LocalStrings
import cz.krokviak.kalky.ui.components.KalkySegmentedControl

@Composable
fun ProfileScene(
    uiState: SettingsUiState,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
    appPreferences: AppPreferences = koinInject()
) {
    val s = LocalStrings.current
    val genderOptions = listOf(s.profile.male, s.profile.female)
    val genderKeys = listOf("Muž", "Žena")
    val activityLabels = listOf(s.profile.sedentary, s.profile.light, s.profile.active, s.profile.veryActive)
    val cardContentInset = 12.dp
    val cardTextSize = 20.sp
    val unitSystem by appPreferences.unitSystem.collectAsState()

    val metricWeightValues = remember {
        (300..2500).map { index ->
            val whole = index / 10
            val frac = index % 10
            "$whole.$frac"
        }
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
            SectionHeader(title = s.profile.personalInfo, startInset = cardContentInset)
            ProfileMeasurementSection(
                uiState = uiState,
                viewModel = viewModel,
                unitSystem = unitSystem,
                metricWeightValues = metricWeightValues,
                metricHeightValues = metricHeightValues,
                ageValues = ageValues,
                displayWeightValues = displayWeightValues,
                displayHeightValues = displayHeightValues,
                textSize = cardTextSize,
            )
        }

        SectionHeader(title = s.profile.gender, startInset = cardContentInset)
        KalkySegmentedControl(
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
            SectionHeader(title = s.profile.activityLevel, startInset = cardContentInset)
            KalkyCard(
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
                            ActivityRowDivider()
                        }
                    }
                }
            }
        }
    }
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
private fun ActivityRowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 12.dp)
            .background(AppTheme.colors.border)
    )
}
