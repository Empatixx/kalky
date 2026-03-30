package cz.krokviak.kalky.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class AppLanguage { CS, EN }

enum class UnitSystem { METRIC, IMPERIAL }

object AppPreferencesManager {
    private const val PREFS_NAME = "kalky_app_prefs"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_UNIT_SYSTEM = "unit_system"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private const val KEY_LAST_NOTIFICATION_TIME = "last_notification_time"
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

    private var sharedPreferences: SharedPreferences? = null

    private val _language = MutableStateFlow(AppLanguage.CS)
    val language: StateFlow<AppLanguage> = _language

    private val _unitSystem = MutableStateFlow(UnitSystem.METRIC)
    val unitSystem: StateFlow<UnitSystem> = _unitSystem

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted

    fun initialize(context: Context) {
        if (sharedPreferences != null) return

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences = prefs
        _language.value = prefs.getEnum(KEY_LANGUAGE, AppLanguage.CS)
        _unitSystem.value = prefs.getEnum(KEY_UNIT_SYSTEM, UnitSystem.METRIC)
        _notificationsEnabled.value = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        _onboardingCompleted.value = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setLanguage(value: AppLanguage) {
        _language.value = value
        sharedPreferences?.edit()?.putString(KEY_LANGUAGE, value.name)?.apply()
    }

    fun setUnitSystem(value: UnitSystem) {
        _unitSystem.value = value
        sharedPreferences?.edit()?.putString(KEY_UNIT_SYSTEM, value.name)?.apply()
    }

    fun setNotificationsEnabled(value: Boolean) {
        _notificationsEnabled.value = value
        sharedPreferences?.edit()?.putBoolean(KEY_NOTIFICATIONS_ENABLED, value)?.apply()
    }

    fun setOnboardingCompleted(value: Boolean) {
        _onboardingCompleted.value = value
        sharedPreferences?.edit()?.putBoolean(KEY_ONBOARDING_COMPLETED, value)?.apply()
    }

    fun getLastNotificationTime(): Long {
        return sharedPreferences?.getLong(KEY_LAST_NOTIFICATION_TIME, 0L) ?: 0L
    }

    fun setLastNotificationTime(timeMillis: Long) {
        sharedPreferences?.edit()?.putLong(KEY_LAST_NOTIFICATION_TIME, timeMillis)?.apply()
    }
}

private inline fun <reified T : Enum<T>> SharedPreferences.getEnum(key: String, default: T): T {
    val stored = getString(key, null) ?: return default
    return enumValues<T>().firstOrNull { it.name == stored } ?: default
}
