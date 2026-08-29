import FirebaseRemoteConfig

enum IosRemoteConfigManager {
    private static let defaultBackendUrl = "http://178.104.95.213"

    static func initialize() {
        let settings = RemoteConfigSettings()
        #if DEBUG
        settings.minimumFetchInterval = 0
        #else
        settings.minimumFetchInterval = 3600
        #endif
        RemoteConfig.remoteConfig().configSettings = settings
        RemoteConfig.remoteConfig().setDefaults([
            "backend_base_url": defaultBackendUrl as NSObject
        ])
        RemoteConfig.remoteConfig().fetchAndActivate()
    }

    static func getBackendBaseUrl() -> String {
        let value = RemoteConfig.remoteConfig()["backend_base_url"].stringValue
        return value.isEmpty ? defaultBackendUrl : value
    }
}
