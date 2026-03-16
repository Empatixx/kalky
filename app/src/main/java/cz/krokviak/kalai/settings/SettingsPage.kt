package cz.krokviak.kalai.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import cz.krokviak.kalai.notifications.MealReminderScheduler
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.theme.ThemeManager
import cz.krokviak.kalai.theme.ThemeMode
import cz.krokviak.kalai.i18n.LocalStrings
import cz.krokviak.kalai.ui.LocalDimensions
import cz.krokviak.kalai.ui.components.KalaiSegmentedControl

@Composable
fun SettingsPage(
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current
    val context = LocalContext.current
    val themeMode by ThemeManager.themeMode.collectAsState()
    val themeLabels = listOf(s.settings.themeSystem, s.settings.themeLight, s.settings.themeDark)
    val themeModes = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)

    val language by AppPreferencesManager.language.collectAsState()
    val languageLabels = listOf("\u010Ce\u0161tina", "English")
    val languageValues = listOf(AppLanguage.CS, AppLanguage.EN)

    val unitSystem by AppPreferencesManager.unitSystem.collectAsState()
    val unitLabels = listOf(s.settings.metric, s.settings.imperial)
    val unitValues = listOf(UnitSystem.METRIC, UnitSystem.IMPERIAL)

    val notificationsEnabled by AppPreferencesManager.notificationsEnabled.collectAsState()
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            AppPreferencesManager.setNotificationsEnabled(true)
            MealReminderScheduler.schedule(context)
        } else {
            AppPreferencesManager.setNotificationsEnabled(false)
        }
    }

    val dims = LocalDimensions.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(dims.screenPadding),
        verticalArrangement = Arrangement.spacedBy(dims.itemSpacing)
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

        Spacer(modifier = Modifier.height(dims.halfSpacing))

        SectionHeader(s.settings.language)
        KalaiSegmentedControl(
            selectedIndex = languageValues.indexOf(language),
            items = languageLabels,
            onItemSelected = { AppPreferencesManager.setLanguage(languageValues[it]) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dims.halfSpacing))

        SectionHeader(s.settings.units)
        KalaiSegmentedControl(
            selectedIndex = unitValues.indexOf(unitSystem),
            items = unitLabels,
            onItemSelected = { AppPreferencesManager.setUnitSystem(unitValues[it]) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dims.halfSpacing))

        // Notifications section
        SectionHeader(s.notifications.reminders)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = s.notifications.enableReminders,
                color = AppTheme.colors.onBackground,
                fontSize = dims.fontBody,
                fontWeight = FontWeight.SemiBold
            )
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                            if (hasPermission) {
                                AppPreferencesManager.setNotificationsEnabled(true)
                                MealReminderScheduler.schedule(context)
                            } else {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        } else {
                            AppPreferencesManager.setNotificationsEnabled(true)
                            MealReminderScheduler.schedule(context)
                        }
                    } else {
                        AppPreferencesManager.setNotificationsEnabled(false)
                        MealReminderScheduler.cancel(context)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AppTheme.colors.onBackground,
                    checkedTrackColor = AppTheme.colors.onBackgroundSecondary,
                    uncheckedThumbColor = AppTheme.colors.onBackgroundSecondary,
                    uncheckedTrackColor = AppTheme.colors.background
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Account section placeholder
        SectionHeader(s.settings.account)

        Text(
            text = s.settings.appVersion,
            color = AppTheme.colors.onBackgroundSecondary,
            fontSize = dims.fontBody,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SectionHeader(title: String) {
    val dims = LocalDimensions.current
    Text(
        text = title,
        color = AppTheme.colors.onBackgroundSecondary,
        fontSize = dims.fontSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp)
    )
}
