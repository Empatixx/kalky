# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
Czech-language food/nutrition tracking app built with Kotlin Multiplatform (KMP) and Compose Multiplatform. All UI screens, ViewModels, repos, network, and DB are in the shared module. The Android app module is a thin shell (MainActivity + platform implementations). iOS app uses ComposeUIViewController to render the same shared UI.

## Build
- **Requires JDK 21** (JDK 25 is NOT compatible with AGP 8.7.2)
- Set `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64` or configure in Android Studio
- Android: `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew :app:compileDebugKotlin`
- Shared module (Android): `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew :shared:compileDebugKotlinAndroid`
- Shared module (iOS): `./gradlew :shared:compileKotlinIosSimulatorArm64` (requires macOS)
- iOS framework: `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64` (requires macOS)
- Backend: `cd backend && bun install && bun run src/index.ts`
- Backend typecheck: `cd backend && bun run tsc --noEmit`

## CI
- **Android CI** (`.github/workflows/android.yml`): Compiles shared + app, runs unit tests on `ubuntu-latest`
- **iOS CI** (`.github/workflows/ios.yml`): Compiles shared for iOS + links framework on `macos-latest`
- **Backend CI** (`.github/workflows/backend.yml`): Typecheck + Docker build on `ubuntu-latest`

## Architecture
- **Pattern**: MVVM + Compose Multiplatform
- **DI**: Koin (sharedModule in commonMain + appModule in Android app + iosModule in iosMain)
- **UI**: Compose Multiplatform with Material3 + custom Kalky components (`shared/.../ui/components/`)
- **Database**: SQLDelight (shared module, multiplatform)
- **HTTP**: Ktor + kotlinx-serialization (shared module)
- **Auth**: Firebase Auth (Google/Apple) — platform-specific implementations behind shared interfaces
- **Date/Time**: kotlinx-datetime (shared module)
- **Preferences**: multiplatform-settings (`AppPreferences` in shared commonMain)

### KMP Boundary
- **shared/commonMain**: ALL Compose UI screens, ViewModels, repositories, network clients, entities, DB, DI, navigation (`AppContent`, `MainScaffold`), theme, i18n, platform interfaces (`PlatformActions`, `AuthTokenProvider`, `AuthStateProvider`, `ImageStorage`, `AppPreferences`)
- **shared/iosMain**: iOS platform implementations (`IosImageStorage`, `IosPlatformActions`, `DriverFactory.ios`, `MainViewController`, `KoinHelper`, stub auth providers)
- **app module (Android)**: Thin shell — `MainActivity.kt` (provides `PlatformActions`), `CameraActivity` (CameraX), `BarcodeScannerActivity` (ML Kit), Firebase implementations (`FirebaseAuthTokenProvider`, `AuthViewModel`), `AppModule` DI
- **iosApp (Swift)**: SwiftUI entry point, `ComposeView` wrapper, `KalkyCameraViewController` (AVFoundation camera + barcode scanning)
- **Rule**: `commonMain` must be platform-independent. Firebase SDK, Android SDK, CameraX, AVFoundation etc. stay in platform-specific modules. Shared module defines interfaces, platform modules implement them.

### Platform Abstractions
- **`PlatformActions`** (`shared/.../common/PlatformActions.kt`): Interface for platform-specific UI actions — `launchCamera()`, `launchBarcodeScanner()`, `signInWithGoogle()`, `signInWithApple()`, `shareImage()`, `requestNotificationPermission()`. Provided via `LocalPlatformActions` CompositionLocal.
- **`AppPreferences`** (`shared/.../common/AppPreferences.kt`): Multiplatform settings (via `russhwolf/multiplatform-settings`). Stores onboarding state, language, unit system, notification prefs. Replaces the old Android-only `AppPreferencesManager`.
- **`AuthViewModelInterface`** (`shared/.../auth/AuthUiState.kt`): Shared auth contract. Android's `AuthViewModel` implements it with Firebase. iOS uses `StubAuthViewModel` in `KoinHelper.kt`; the actual auth flow happens in Swift (`GoogleSignInHelper`, `AppleSignInHelper`) and is bridged via `IosAuthTokenProvider`/`IosAuthStateProvider`.
- **`ImageStorage`** (`shared/.../common/ImageStorage.kt`): File-based image storage. Android uses external files dir, iOS uses NSDocumentDirectory.

### Auth Flow
- Firebase Auth on Android (Credential Manager for Google), platform callbacks via `PlatformActions`
- `FirebaseAuthTokenProvider` implements shared `AuthTokenProvider` interface
- Ktor HttpClient auto-attaches Firebase ID token via interceptor in `HttpClientFactory.kt` (only for non-OpenFoodFacts URLs)
- Backend verifies tokens via `firebase-admin` in `requireAuth()` middleware
- Navigation: Onboarding → LoginRoute → DefaultRoute. Returning users skip to DefaultRoute if Firebase session persists
- `POST /api/auth/me` — idempotent user registration/lookup (called after sign-in)

### Navigation
- Type-safe routes with `@Serializable` objects/data classes in `NavRoutes.kt` (shared commonMain)
- `AppContent` composable in `shared/.../app/AppContent.kt` — contains full NavHost
- `MainActivity.kt` provides `PlatformActions` via CompositionLocal, then calls shared `AppContent`
- iOS `MainViewController.kt` does the same via `ComposeUIViewController`
- Start destination determined by: `AppPreferences.onboardingCompleted` → `AuthStateProvider.isAuthenticated` → DefaultRoute
- MainScaffold uses HorizontalPager with 4 pages (Home, Analytics, Profile, Settings)

## Cognitive Complexity (business logic only)

- **Max 1 level of nesting** inside any non-Composable function. **Composables are exempt** — Compose layout DSL (`Column { Row { Box { ... } } }`) is structural, not control flow.
- The rule applies to: ViewModels, use cases (`common/domain/`), repositories (`common/repo/`), utilities (`common/utils/`, `common/`), domain helpers (`StreakCalculator`, `FoodPhotoAnalyzer`, `PhotoCaptureController`, etc.), and network layer.
- "Nesting" means stacking **branching** control flow: `if`/`else`/`when` inside `if`/`when`, `for`/`while` inside a conditional, `try` inside a conditional, a `runCatching { }` lambda that itself contains `if`/`when`. **Scope openers do not count as a nesting level** — `viewModelScope.launch { }`, `coroutineScope { }`, `_uiState.update { }`, `withContext { }`, `?.let { }` are wrappers, not branches. A single `if/when/for` directly inside a `launch { }` is level 1, fine. Two stacked branching structures (e.g., `if` inside `if`, or `when` inside `for`) is level 2, forbidden.
- Refactoring patterns:
  - **Early return / guard clauses** (`x ?: return`, `if (!ok) return`) instead of `if (ok) { ... } else { ... }` with branches inside.
  - **Extract method** when a branch contains another branch — pull it into a private helper.
  - **`when` chain** instead of stacked `if/else if/else`.
  - **Map/lookup** instead of conditional ladders matching discrete values.
  - **Flatten lambdas**: prefer `flow.collect { handleX(it) }` over `flow.collect { if (...) ... else ... }`.
- Applies to all new or refactored code in `shared/src/commonMain/` outside the `*Scene.kt`, `*Page.kt`, and `ui/components/` Compose layers.

## Key Conventions
- UI language: Czech (i18n system in `shared/.../i18n/Strings.kt` with `CzechStrings`/`EnglishStrings`, `LocalStrings.current`)
- **UI style**: iOS-inspired design. Use custom Kalky components — `KalkyButton`, `KalkyCard`, `KalkySegmentedControl`, `KalkyGradientBackground` (all in `shared/.../ui/components/`). Colors via `AppTheme.colors`. Macro colors via `MacroColors` (`shared/.../theme/MacroColors.kt`). Responsive sizing via `LocalDimensions.current`. Prefer iOS UX patterns (smooth transitions, bottom sheets, minimal chrome) over Material defaults.
- **Domain logic belongs in ViewModels**, not in composables. Composables are pure UI — they call ViewModel methods.
- Feature structure: `FeatureName/` with `FeatureScene.kt`, `FeatureViewModel.kt`, `FeatureUiState.kt`, `components/`
- Colors: Black/White/Gray theme via `AppTheme.colors`
- **No `java.util.*` in commonMain** — use `formatFloat1()` from `common/FormatUtils.kt` instead of `String.format(Locale.US, ...)`
- **No Android resources in commonMain** — use Material Icons instead of `R.drawable.*`, `MacroColors` instead of `R.color.*`
- **Koin injection in composables**: use `koinInject<T>()` from `shared/.../di/KoinInject.kt` (uses `KoinPlatformTools.defaultContext()`)

## APIs
- **Backend** (`backend/`): Bun + SQLite server, default port 3000
  - `POST /cal` — food image analysis (raw image bytes → nutrition JSON via OpenAI vision)
  - `GET /api/barcode/:code` — product lookup by barcode
  - `GET /api/search?q=...` — text search, max 20 results
  - `POST /api/auth/me` — authenticated user registration/profile (requires Firebase ID token)
  - `POST /api/admin/import` — bulk product import (requires ADMIN_KEY)
- **Open Food Facts** (fallback): `GET https://world.openfoodfacts.org/api/v2/product/{barcode}.json`

## Backend Details
- Runtime: Bun with `bun:sqlite`, no framework (raw `Bun.serve` routing)
- Auth middleware: `requireAuth()` (Firebase Admin SDK) and `requireAdmin()` (static ADMIN_KEY)
- Database: `products` table + FTS5 for Czech text search, `users` table (firebase_uid, email, display_name)
- Environment: `OPENAI_API_KEY`, `ADMIN_KEY`, `GOOGLE_APPLICATION_CREDENTIALS` (Firebase service account JSON)
