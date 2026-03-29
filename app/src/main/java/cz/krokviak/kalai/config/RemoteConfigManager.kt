package cz.krokviak.kalai.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

object RemoteConfigManager {
    private const val KEY_BACKEND_BASE_URL = "backend_base_url"
    private const val DEFAULT_BACKEND_BASE_URL = "http://178.104.95.213"

    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    fun init() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (cz.krokviak.kalai.BuildConfig.DEBUG) 0 else 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf(KEY_BACKEND_BASE_URL to DEFAULT_BACKEND_BASE_URL))
        remoteConfig.fetchAndActivate()
    }

    fun getBackendBaseUrl(): String {
        return remoteConfig.getString(KEY_BACKEND_BASE_URL).ifEmpty { DEFAULT_BACKEND_BASE_URL }
    }
}
