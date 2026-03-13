package cz.krokviak.kalai.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.theme.ThemeManager
import cz.krokviak.kalai.theme.ThemeMode
import cz.krokviak.kalai.i18n.LocalStrings
import cz.krokviak.kalai.ui.components.KalaiSegmentedControl

@Composable
fun SettingsPage(
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current
    val themeMode by ThemeManager.themeMode.collectAsState()
    val themeLabels = listOf(s.settings.themeSystem, s.settings.themeLight, s.settings.themeDark)
    val themeModes = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)

    val language by AppPreferencesManager.language.collectAsState()
    val languageLabels = listOf("\u010Ce\u0161tina", "English")
    val languageValues = listOf(AppLanguage.CS, AppLanguage.EN)

    val unitSystem by AppPreferencesManager.unitSystem.collectAsState()
    val unitLabels = listOf(s.settings.metric, s.settings.imperial)
    val unitValues = listOf(UnitSystem.METRIC, UnitSystem.IMPERIAL)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = s.settings.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = AppTheme.colors.onBackground
        )

        // Theme section
        SectionHeader(s.settings.appearance)

        KalaiSegmentedControl(
            selectedIndex = themeModes.indexOf(themeMode),
            items = themeLabels,
            onItemSelected = { ThemeManager.setThemeMode(themeModes[it]) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        SectionHeader(s.settings.language)
        KalaiSegmentedControl(
            selectedIndex = languageValues.indexOf(language),
            items = languageLabels,
            onItemSelected = { AppPreferencesManager.setLanguage(languageValues[it]) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        SectionHeader(s.settings.units)
        KalaiSegmentedControl(
            selectedIndex = unitValues.indexOf(unitSystem),
            items = unitLabels,
            onItemSelected = { AppPreferencesManager.setUnitSystem(unitValues[it]) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Account section placeholder
        SectionHeader(s.settings.account)

        Text(
            text = s.settings.appVersion,
            color = AppTheme.colors.onBackgroundSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = AppTheme.colors.onBackgroundSecondary,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp)
    )
}
