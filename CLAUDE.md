# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
Czech-language food/nutrition tracking app built with Kotlin Multiplatform (KMP). Business logic (ViewModels, repos, network, DB) is in the shared module. UI Compose screens remain in the Android app module. iOS app skeleton is set up.

## Build
- **Requires JDK 21** (JDK 25 is NOT compatible with AGP 8.7.2)
- Set `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64` or configure in Android Studio
- Build: `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew :app:compileDebugKotlin`
- Shared module only: `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew :shared:compileDebugKotlinAndroid`
- Backend: `cd backend && bun install && bun run src/index.ts`
- Backend typecheck: `cd backend && bun run tsc --noEmit`

## Architecture
- **Pattern**: MVVM + Compose
- **DI**: Koin (sharedModule in commonMain + appModule in Android app)
- **UI**: Compose with Material3 + custom Kalai components (`ui/components/`)
- **Database**: SQLDelight (shared module, multiplatform)
- **HTTP**: Ktor + kotlinx-serialization (shared module)
- **Auth**: Firebase Auth (Google/Apple/Email) — client SDK in app module, Firebase Admin SDK on backend
- **Date/Time**: kotlinx-datetime (shared module)

### KMP Boundary
- **shared/commonMain**: ViewModels, repositories, network clients, entities, DB, DI, interfaces (AuthTokenProvider, AuthStateProvider, ImageStorage)
- **app module (Android)**: All Compose UI screens, platform implementations (FirebaseAuthTokenProvider, AndroidImageStorage, CameraX, ML Kit), Activity classes, AppModule DI
- **Rule**: `commonMain` must be platform-independent. Firebase SDK, Android SDK, CameraX etc. stay in app module. Shared module defines interfaces, app module implements them.

### Auth Flow
- Firebase Auth on Android (Credential Manager for Google, OAuthProvider for Apple, email/password)
- `FirebaseAuthTokenProvider` implements shared `AuthTokenProvider` interface
- Ktor HttpClient auto-attaches Firebase ID token via interceptor in `HttpClientFactory.kt` (only for non-OpenFoodFacts URLs)
- Backend verifies tokens via `firebase-admin` in `requireAuth()` middleware
- Navigation: Onboarding → LoginRoute → DefaultRoute. Returning users skip to DefaultRoute if Firebase session persists
- `POST /api/auth/me` — idempotent user registration/lookup (called after sign-in)

### Navigation
- Type-safe routes with `@Serializable` objects/data classes in `NavRoutes.kt`
- NavHost in `MainActivity.kt` → `AppContent` composable
- Start destination determined by: onboarding completed? → authenticated? → DefaultRoute
- MainScaffold uses HorizontalPager with 4 pages (Home, Analytics, Profile, Settings)

## Key Conventions
- UI language: Czech (i18n system in `i18n/Strings.kt` with `CzechStrings`/`EnglishStrings`, `LocalStrings.current`)
- **UI style**: iOS-inspired design. Use custom Kalai components — `KalaiButton`, `KalaiCard`, `KalaiSegmentedControl`, `KalaiGradientBackground`. Colors via `AppTheme.colors`. Responsive sizing via `LocalDimensions.current`. For pickers, build custom iOS-style wheel pickers using `LazyColumn` + `rememberSnapFlingBehavior` (see `analytics/components/WheelDatePicker.kt`). Prefer iOS UX patterns (smooth transitions, bottom sheets, minimal chrome) over Material defaults.
- **Domain logic belongs in ViewModels**, not in composables. Composables are pure UI — they call ViewModel methods.
- Feature structure: `FeatureName/` with `FeatureScene.kt`, `FeatureViewModel.kt`, `FeatureUiState.kt`, `components/`
- Colors: Black/White/Gray theme via `AppTheme.colors`

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
