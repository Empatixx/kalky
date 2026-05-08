package cz.krokviak.kalky.scenes.settings

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.scenes.auth.AuthUser
import cz.krokviak.kalky.core.common.AppLanguage
import cz.krokviak.kalky.core.common.AppPreferences
import cz.krokviak.kalky.core.common.LocalPlatformActions
import cz.krokviak.kalky.core.common.UnitSystem
import cz.krokviak.kalky.core.di.koinInject
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.core.ui.components.KalkyCard
import cz.krokviak.kalky.core.theme.ThemeManager
import cz.krokviak.kalky.core.theme.ThemeMode
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.core.ui.LocalDimensions
import cz.krokviak.kalky.core.ui.components.KalkySegmentedControl

@Composable
fun SettingsScene(
    modifier: Modifier = Modifier,
    authUser: AuthUser? = null,
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    appPreferences: AppPreferences = koinInject()
) {
    val s = LocalStrings.current
    val platformActions = LocalPlatformActions.current
    val themeMode by ThemeManager.themeMode.collectAsState()
    val themeLabels = listOf(s.settings.themeSystem, s.settings.themeLight, s.settings.themeDark)
    val themeModes = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)

    val language by appPreferences.language.collectAsState()
    val languageLabels = listOf("\u010Ce\u0161tina", "English")
    val languageValues = listOf(AppLanguage.CS, AppLanguage.EN)

    val unitSystem by appPreferences.unitSystem.collectAsState()
    val unitLabels = listOf(s.settings.metric, s.settings.imperial)
    val unitValues = listOf(UnitSystem.METRIC, UnitSystem.IMPERIAL)

    val notificationsEnabled by appPreferences.notificationsEnabled.collectAsState()

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

        // Account card
        if (authUser != null) {
            KalkyCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = AppTheme.colors.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(AppTheme.colors.surfaceSecondary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            val initials = authUser.displayName
                                ?.split(" ")
                                ?.take(2)
                                ?.mapNotNull { it.firstOrNull()?.uppercase() }
                                ?.joinToString("")
                            if (!initials.isNullOrEmpty()) {
                                Text(
                                    text = initials,
                                    color = AppTheme.colors.onBackground,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = AppTheme.colors.onBackgroundSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = authUser.displayName ?: authUser.email ?: "",
                                color = AppTheme.colors.onBackground,
                                fontSize = dims.fontBody,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (authUser.email != null && authUser.displayName != null) {
                                Text(
                                    text = authUser.email!!,
                                    color = AppTheme.colors.onBackgroundSecondary,
                                    fontSize = dims.fontSmall
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .height(1.dp)
                            .background(AppTheme.colors.border)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSignOutClick() }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = s.auth.signOut,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = dims.fontBody,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Theme section
        SectionHeader(s.settings.appearance)

        KalkySegmentedControl(
            selectedIndex = themeModes.indexOf(themeMode),
            items = themeLabels,
            onItemSelected = { ThemeManager.setThemeMode(themeModes[it]) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dims.halfSpacing))

        SectionHeader(s.settings.language)
        KalkySegmentedControl(
            selectedIndex = languageValues.indexOf(language),
            items = languageLabels,
            onItemSelected = { appPreferences.setLanguage(languageValues[it]) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dims.halfSpacing))

        SectionHeader(s.settings.units)
        KalkySegmentedControl(
            selectedIndex = unitValues.indexOf(unitSystem),
            items = unitLabels,
            onItemSelected = { appPreferences.setUnitSystem(unitValues[it]) },
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
                        if (platformActions.isNotificationPermissionGranted()) {
                            appPreferences.setNotificationsEnabled(true)
                        } else {
                            platformActions.requestNotificationPermission()
                        }
                    } else {
                        appPreferences.setNotificationsEnabled(false)
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

        Spacer(modifier = Modifier.height(dims.halfSpacing))

        // Legal section
        SectionHeader(s.legal.sectionTitle)

        LegalRow(label = s.legal.termsTitle, onClick = onTermsClick)
        LegalRow(label = s.legal.privacyTitle, onClick = onPrivacyClick)

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = s.settings.appVersion,
            color = AppTheme.colors.onBackgroundSecondary,
            fontSize = dims.fontSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun LegalRow(label: String, onClick: () -> Unit) {
    val dims = LocalDimensions.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = AppTheme.colors.onBackground,
            fontSize = dims.fontBody,
            fontWeight = FontWeight.SemiBold
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = AppTheme.colors.onBackgroundSecondary
        )
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
