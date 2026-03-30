# Add Firebase Remote Config, FCM, Performance Monitoring, App Check

## Context

Kalky uses Firebase for Crashlytics, Analytics, and Auth. The backend runs on Hetzner (Bun + SQLite). Four additional free Firebase services will improve the app: Remote Config for dynamic backend URL, FCM for server-pushed notifications, Performance Monitoring for automatic instrumentation, and App Check to protect the backend from unauthorized callers.

## Current State

- Firebase BOM `33.7.0` already in `app/build.gradle.kts`
- `google-services.json` configured with Auth, Crashlytics, Analytics
- Backend at `backend/` uses `firebase-admin` for token verification
- Backend URL hardcoded as `http://192.168.0.115:3000` in `FoodAnalysisClient` and `AuthViewModel`
- Local notifications via WorkManager (`MealReminderScheduler`)
- No performance monitoring
- No API request verification (any client can call the backend)

---

## 1. Remote Config

### Purpose
Store the backend base URL remotely so it can be changed without app updates (dev/staging/prod, Hetzner migration).

### Changes

**Dependencies** (`app/build.gradle.kts`):
```kotlin
implementation("com.google.firebase:firebase-config")
```

**New file**: `app/src/main/java/cz/krokviak/kalky/config/RemoteConfigManager.kt`
- Singleton object, initialized in `KalkyApplication.onCreate()`
- Sets default: `backend_base_url` = `"http://192.168.0.115:3000"`
- Calls `fetchAndActivate()` on init
- Exposes `fun getBackendBaseUrl(): String` reading from Remote Config
- Minimum fetch interval: 3600s (1 hour) in production, 0s in debug

**Modify**: `shared/src/commonMain/kotlin/cz/krokviak/kalky/network/FoodAnalysisClient.kt`
- Change `DEFAULT_BASE_URL` from hardcoded string to constructor parameter `baseUrl: String`
- Already accepts `baseUrl` parameter — just need DI to pass Remote Config value

**Modify**: `app/src/main/java/cz/krokviak/kalky/auth/AuthViewModel.kt`
- Replace hardcoded `"http://192.168.0.115:3000/api/auth/me"` with `RemoteConfigManager.getBackendBaseUrl() + "/api/auth/me"`

**Modify**: `shared/src/commonMain/kotlin/cz/krokviak/kalky/di/SharedModule.kt`
- `FoodAnalysisClient` already receives `baseUrl` in constructor — pass it from DI

**Modify**: `app/src/main/java/cz/krokviak/kalky/di/AppModule.kt`
- Provide `backend_base_url` string from `RemoteConfigManager` to the shared module's `FoodAnalysisClient`

**Firebase Console**: Create Remote Config parameter `backend_base_url` with default value.

---

## 2. FCM Push Notifications

### Purpose
Enable server-triggered push notifications (e.g., daily reminders, weekly summaries, announcements). More powerful than local WorkManager — can target specific users, send from backend.

### Android Changes

**Dependencies** (`app/build.gradle.kts`):
```kotlin
implementation("com.google.firebase:firebase-messaging")
```

**New file**: `app/src/main/java/cz/krokviak/kalky/notifications/KalkyFcmService.kt`
- Extends `FirebaseMessagingService`
- `onNewToken(token)`: sends token to backend via `POST /api/auth/fcm-token`
- `onMessageReceived(message)`: shows notification using existing `NotificationHelper`

**Modify**: `app/src/main/AndroidManifest.xml`
- Register `KalkyFcmService` with intent filter for `com.google.firebase.MESSAGING_EVENT`

**Modify**: `app/src/main/java/cz/krokviak/kalky/KalkyApplication.kt`
- After auth state confirmed, retrieve current FCM token via `FirebaseMessaging.getInstance().token` and send to backend

### Backend Changes

**Modify**: `backend/src/db/schema.ts`
- Add `fcm_token TEXT` column to `users` table

**Modify**: `backend/src/db/users.ts`
- Add `updateFcmToken(firebaseUid: string, fcmToken: string)` function

**New file**: `backend/src/routes/fcm.ts`
- `POST /api/auth/fcm-token` — protected by `requireAuth`, body: `{ token: string }`
- Calls `updateFcmToken` with the verified user's UID

**Modify**: `backend/src/index.ts`
- Register `POST /api/auth/fcm-token` route

Backend can later use `admin.messaging().send()` to push notifications to specific users by their stored FCM token. This is not implemented now — just the token storage infrastructure.

---

## 3. Performance Monitoring

### Purpose
Automatic instrumentation of screen rendering, HTTP request latency, and app startup time. Zero code changes.

### Changes

**Dependencies** (`app/build.gradle.kts`):
```kotlin
implementation("com.google.firebase:firebase-perf")
```

**Plugin** (`app/build.gradle.kts` plugins block):
```kotlin
id("com.google.firebase.firebase-perf") // add to plugins
```

**Root** `build.gradle.kts`:
```kotlin
alias(libs.plugins.firebase.perf) apply false // add to plugins
```

**Version catalog** (`gradle/libs.versions.toml`):
- Add `firebasePerf` version and plugin entry

That's it. The Gradle plugin auto-instruments:
- App startup trace
- Screen rendering (slow/frozen frames)
- All HTTP requests via OkHttp (Ktor uses OkHttp on Android)

No Kotlin code changes required.

---

## 4. App Check

### Purpose
Verify that API requests come from the genuine Kalky app, not scrapers or unauthorized clients. Protects the `/cal` endpoint (which costs money via OpenAI).

### Android Changes

**Dependencies** (`app/build.gradle.kts`):
```kotlin
implementation("com.google.firebase:firebase-appcheck")
implementation("com.google.firebase:firebase-appcheck-playintegrity")
```

**Modify**: `app/src/main/java/cz/krokviak/kalky/KalkyApplication.kt`
- Initialize App Check with Play Integrity provider:
```kotlin
FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
    PlayIntegrityAppCheckProviderFactory.getInstance()
)
```

**Modify**: `shared/src/commonMain/kotlin/cz/krokviak/kalky/network/HttpClientFactory.kt`
- Add App Check token to requests (alongside auth token) via the interceptor
- New interface `AppCheckTokenProvider` in shared module with `suspend fun getToken(): String?`
- Android implementation wraps `FirebaseAppCheck.getInstance().getAppCheckToken()`

### Backend Changes

**Modify**: `backend/src/middleware/auth.ts`
- Add `requireAppCheck(req: Request)` function
- Verifies `X-Firebase-AppCheck` header using `admin.appCheck().verifyToken(token)`
- Returns 401 if invalid

**Modify**: `backend/src/index.ts`
- Apply `requireAppCheck` to `/cal` endpoint (most expensive — uses OpenAI)
- Optionally apply to other endpoints later

**Firebase Console**: Enable App Check with Play Integrity provider for the Android app.

---

## Files Summary

| File | Action | Service |
|------|--------|---------|
| `app/build.gradle.kts` | Modify — add 4 dependencies + perf plugin | All |
| `build.gradle.kts` | Modify — add perf plugin | Perf |
| `gradle/libs.versions.toml` | Modify — add perf plugin version | Perf |
| `app/.../config/RemoteConfigManager.kt` | New | Remote Config |
| `app/.../notifications/KalkyFcmService.kt` | New | FCM |
| `app/.../KalkyApplication.kt` | Modify — init Remote Config, App Check, FCM token | RC, AppCheck, FCM |
| `app/.../auth/AuthViewModel.kt` | Modify — use Remote Config URL | Remote Config |
| `app/.../di/AppModule.kt` | Modify — provide base URL from RC | Remote Config |
| `shared/.../network/HttpClientFactory.kt` | Modify — add App Check header | App Check |
| `shared/.../auth/AppCheckTokenProvider.kt` | New — interface | App Check |
| `app/.../auth/FirebaseAppCheckTokenProvider.kt` | New — Android impl | App Check |
| `app/src/main/AndroidManifest.xml` | Modify — register FCM service | FCM |
| `backend/src/db/schema.ts` | Modify — add fcm_token column | FCM |
| `backend/src/db/users.ts` | Modify — add updateFcmToken | FCM |
| `backend/src/routes/fcm.ts` | New | FCM |
| `backend/src/index.ts` | Modify — register FCM route | FCM |
| `backend/src/middleware/auth.ts` | Modify — add requireAppCheck | App Check |

## Verification

1. **Remote Config**: Change `backend_base_url` in Firebase Console → restart app → verify API calls go to new URL
2. **FCM**: Send test message from Firebase Console → Messaging → verify notification appears
3. **Performance**: Check Firebase Console → Performance after running app for a few minutes
4. **App Check**: Call `/cal` without App Check token → expect 401. Call with token → expect success
5. **Build**: `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew :app:compileDebugKotlin`
6. **Backend**: `cd backend && bun run tsc --noEmit`
