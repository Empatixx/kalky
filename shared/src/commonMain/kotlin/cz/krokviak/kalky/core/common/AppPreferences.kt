package cz.krokviak.kalky.core.common

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class UnitSystem { METRIC, IMPERIAL }
enum class AppLanguage(val displayName: String) {
    CS("\u010Ce\u0161tina"),
    EN("English")
}

class AppPreferences(private val settings: Settings = Settings()) {
    private val _language = MutableStateFlow(
        try { AppLanguage.valueOf(settings.getString("language", "CS")) }
        catch (_: Exception) { AppLanguage.CS }
    )
    val language: StateFlow<AppLanguage> = _language

    private val _unitSystem = MutableStateFlow(
        try { UnitSystem.valueOf(settings.getString("unit_system", "METRIC")) }
        catch (_: Exception) { UnitSystem.METRIC }
    )
    val unitSystem: StateFlow<UnitSystem> = _unitSystem

    private val _notificationsEnabled = MutableStateFlow(
        settings.getBoolean("notifications_enabled", true)
    )
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    private val _onboardingCompleted = MutableStateFlow(
        settings.getBoolean("onboarding_completed", false)
    )
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted

    fun setLanguage(value: AppLanguage) {
        settings.putString("language", value.name)
        _language.value = value
    }

    fun setUnitSystem(value: UnitSystem) {
        settings.putString("unit_system", value.name)
        _unitSystem.value = value
    }

    fun setNotificationsEnabled(value: Boolean) {
        settings.putBoolean("notifications_enabled", value)
        _notificationsEnabled.value = value
    }

    fun setOnboardingCompleted(value: Boolean) {
        settings.putBoolean("onboarding_completed", value)
        _onboardingCompleted.value = value
    }

    var lastNotificationTime: Long
        get() = settings.getLong("last_notification_time", 0L)
        set(value) = settings.putLong("last_notification_time", value)
}
