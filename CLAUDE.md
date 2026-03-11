# Kalai - Food Tracking App

## Project Overview
Czech-language food/nutrition tracking app built with Kotlin Multiplatform (KMP). Business logic (ViewModels, repos, network, DB) is in the shared module. UI Compose screens remain in the Android app module. iOS app skeleton is set up.

## Architecture
- **Pattern**: MVVM + Compose
- **DI**: Koin
- **UI**: Compose with Material3 + custom Kalai components (`ui/components/`)
- **Database**: SQLDelight (shared module, multiplatform)
- **HTTP**: Ktor + kotlinx-serialization (shared module)
- **Date/Time**: kotlinx-datetime (shared module)

## Project Structure
```
kalai/
├── app/                          # Android app module
│   └── src/main/java/cz/krokviak/kalai/
│       ├── home/                 # MainActivity, HomeScene, UI components
│       ├── analytics/            # AnalyticsScene, chart components
│       ├── settings/             # SettingsPage UI
│       ├── detail/               # FoodDetailScene, UI components
│       ├── nutrientedit/         # NutrientEditScene, UI components
│       ├── camera/               # CameraX (Activity, ViewModel, Screen)
│       ├── barcode/              # ML Kit scanner (Activity, Screen)
│       ├── ui/components/         # KalaiCard, KalaiButton, KalaiSegmentedControl
│       ├── common/               # AndroidImageStorage
│       └── di/                   # appModule (platform DI + ViewModels)
├── shared/                       # KMP shared module
│   └── src/
│       ├── commonMain/           # ViewModels, repos, network, entities, DB, DI
│       ├── androidMain/          # DriverFactory, Platform
│       └── iosMain/              # DriverFactory, KoinHelper, Platform
├── iosApp/                       # iOS app (SwiftUI entry point)
│   └── iosApp/
```

## KMP Migration Status
- [x] Phase 0: Gradle setup (KMP plugins, new dependencies)
- [x] Phase 1: Shared module skeleton
- [x] Phase 2: ThreeTenABP → kotlinx-datetime
- [x] Phase 3: Retrofit + Jackson → Ktor + kotlinx-serialization
- [x] Phase 4: Room → SQLDelight
- [x] Phase 5: Koin → Koin Multiplatform (sharedModule + appModule split)
- [x] Phase 6: ViewModels & state classes moved to shared/commonMain
- [x] Phase 7: Camera/Barcode abstraction (stays platform-specific, shared VMs handle results)
- [x] Phase 8: MainActivity thin wrapper (Activity handles camera/barcode intents, delegates to shared VMs)
- [x] Phase 9: iOS app module skeleton (iosApp/, KoinHelper, SwiftUI entry point)

## Build
- **Requires JDK 21** (JDK 25 is NOT compatible with AGP 8.7.2)
- Set `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64` or configure in Android Studio
- Build: `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew :app:compileDebugKotlin`

## Key Conventions
- UI language: Czech
- **UI style**: Use custom Kalai components from `ui/components/` — `KalaiCard` (card container), `KalaiButton` (filled button), `KalaiSegmentedControl` (tab selector). For text/icons use Material3 `Text` and `Icon`. For pickers, build custom iOS-style pickers using `LazyColumn` + `rememberSnapFlingBehavior` (see `analytics/components/WheelDatePicker.kt`).
- Feature structure: `FeatureName/` with `FeatureScene.kt`, `FeatureViewModel.kt`, `FeatureUiState.kt`, `components/`
- Colors: Black/White/Gray theme via `AppTheme.colors`
- Navigation: Type-safe routes with `@Serializable` objects/data classes in `NavRoutes.kt`
- Bottom nav: 3 tabs (Domov, Analýza, Nastavení) + FABs (camera + barcode scanner)

## APIs
- **Food analysis**: `POST http://192.168.0.115:8080/cal` (local backend, sends image bytes)
- **Barcode lookup**: Open Food Facts `GET https://world.openfoodfacts.org/api/v2/product/{barcode}.json`

## Platform-specific code (stays in app module)
- CameraX (CameraActivity, CameraViewModel, CameraScreen)
- ML Kit barcode scanning (BarcodeScannerActivity, BarcodeScannerScreen)
- AndroidImageStorage (implements shared ImageStorage interface)
- All Compose UI screens (HomeScene, AnalyticsScene, SettingsPage, FoodDetailScene, NutrientEditScene)
- AndroidManifest.xml, KalaiApplication.kt, MainActivity.kt
- AppModule (DriverFactory, AndroidImageStorage, ViewModel registrations)

## Shared module contents (commonMain)
- All ViewModels (MainViewModel, FoodDetailViewModel, NutrientEditViewModel, AnalyticsViewModel, SettingsViewModel, BarcodeScannerViewModel)
- All UI state classes (MainUiState, FoodDetailState, NutrientEditState, AnalyticsUiState, SettingsUiState)
- Repositories (FoodRepository, PersonalInfoRepo, NutrientSettingRepo)
- Network clients (FoodAnalysisClient, OpenFoodFactsClient)
- SQLDelight database (KalaiDatabase, .sq files)
- ImageStorage interface, NavRoutes, DateTimeUtils
- SharedModule (Koin DI)
