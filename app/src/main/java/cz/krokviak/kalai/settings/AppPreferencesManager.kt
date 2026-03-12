package cz.krokviak.kalai.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class AppLanguage { CS, EN }

enum class UnitSystem { METRIC, IMPERIAL }

object AppPreferencesManager {
    private const val PREFS_NAME = "kalai_app_prefs"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_UNIT_SYSTEM = "unit_system"

    private var sharedPreferences: SharedPreferences? = null

    private val _language = MutableStateFlow(AppLanguage.CS)
    val language: StateFlow<AppLanguage> = _language

    private val _unitSystem = MutableStateFlow(UnitSystem.METRIC)
    val unitSystem: StateFlow<UnitSystem> = _unitSystem

    fun initialize(context: Context) {
        if (sharedPreferences != null) return

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences = prefs
        _language.value = prefs.getEnum(KEY_LANGUAGE, AppLanguage.CS)
        _unitSystem.value = prefs.getEnum(KEY_UNIT_SYSTEM, UnitSystem.METRIC)
    }

    fun setLanguage(value: AppLanguage) {
        _language.value = value
        sharedPreferences?.edit()?.putString(KEY_LANGUAGE, value.name)?.apply()
    }

    fun setUnitSystem(value: UnitSystem) {
        _unitSystem.value = value
        sharedPreferences?.edit()?.putString(KEY_UNIT_SYSTEM, value.name)?.apply()
    }
}

private inline fun <reified T : Enum<T>> SharedPreferences.getEnum(key: String, default: T): T {
    val stored = getString(key, null) ?: return default
    return enumValues<T>().firstOrNull { it.name == stored } ?: default
}
