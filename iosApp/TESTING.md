# iOS Testing — Kalky

Quick reference for testing the iOS app and the shared KMP module on the iOS target.

## Layers

| Layer | Where | Tooling | Status |
|---|---|---|---|
| Shared business logic (Kotlin) | `shared/src/commonTest/` | kotlin.test + Turbine + Mokkery + ktor-client-mock | ✅ 156 tests, runs on iOS via `iosSimulatorArm64Test` |
| Shared Compose UI (Kotlin) | `shared/src/commonTest/` (Robolectric) | runComposeUiTest + Robolectric | ✅ 7 tests on Android JVM; iOS-side runner deferred |
| Native iOS (Swift) | `iosApp/iosAppTests/` | XCTest | ❌ No target yet — see "Adding XCTest" below |
| iOS UI (Swift) | `iosApp/iosAppUITests/` | XCUITest | ❌ Deferred |

## Running shared tests against iOS Simulator

Requires macOS + Xcode + an iOS Simulator runtime.

```bash
./gradlew :shared:iosSimulatorArm64Test
```

This runs the same `commonTest` suite (network clients, ViewModels, FoodPhotoAnalyzer pipeline, repository CRUD, BMI category, etc.) compiled to Kotlin/Native and executed inside an iOS simulator. It catches KMP-only regressions like:

- `expect`/`actual` mismatches between `androidMain` and `iosMain`
- Native-incompatible reflection (`Class.forName`, etc.) that breaks only on iOS
- Mokkery / Turbine differences across targets

CI runs this step on `macos-latest` — see `.github/workflows/ios.yml`.

## Adding XCTest (Swift unit tests)

Not yet wired up. The Xcode project lives in `iosApp/iosApp.xcodeproj` (open it once on macOS to add targets — automating Xcode project-file edits from CLI is fragile).

### Recommended target structure

```
iosApp/
├── iosApp/                   # main app target
│   ├── Auth/
│   ├── Camera/
│   └── Notifications/
├── iosAppTests/              # NEW — XCTest unit tests
│   ├── AuthHelpersTests.swift
│   ├── NotificationManagerTests.swift
│   └── IosImageStorageTests.swift
└── iosAppUITests/            # NEW — XCUITest UI tests
    └── LoginFlowUITests.swift
```

### What's worth testing in Swift

- **GoogleSignInHelper / AppleSignInHelper** — mock `Auth.auth()` via dependency injection, verify the credential flow path (success / cancel / error)
- **NotificationManager** — `requestPermission`, `scheduleMealReminder` mocking `UNUserNotificationCenter`. The new FCM-token-to-backend POST path is a good integration test target
- **IosRemoteConfigManager** — fallback URL when Firebase fetch fails
- **IosImageStorage** (Kotlin from iosMain — but Swift can call it via `import shared` to verify the contract)

### Sample XCTest

```swift
import XCTest
@testable import iosApp

final class NotificationManagerTests: XCTestCase {
    func test_scheduleMealReminder_addsRequestToCenter() {
        let manager = NotificationManager()
        manager.scheduleMealReminder(title: "Eat!", body: "Time to log lunch")

        let exp = expectation(description: "pending request")
        UNUserNotificationCenter.current().getPendingNotificationRequests { requests in
            XCTAssertTrue(requests.contains { $0.content.title == "Eat!" })
            exp.fulfill()
        }
        wait(for: [exp], timeout: 1)
    }
}
```

Wire it into the Xcode project via **File → New → Target → Unit Testing Bundle**, then add to the existing scheme.

## Compose UI tests on iOS target (deferred)

`runComposeUiTest` in `commonTest` works for Android JVM via Robolectric. For iOS, Compose Multiplatform 1.8 ships an iOS test runtime, but it requires:

- `iosTest` source set wired up in `shared/build.gradle.kts`
- Specific iOS Simulator boot in CI (`xcrun simctl boot`)
- Mokkery 3.x for full Native mock support (we're on 2.10)

Plan revisits this when CMP 1.11 stabilizes the v2 test API and Mokkery 3 KMP support is broadly tested.

## Manual smoke test before release

Until automation lands, run through:

1. Sign in with Google → backend `/api/auth/me` returns 200 (check Xcode logs for "FCM token sync" success)
2. Sign in with Apple → same
3. Launch camera → take a photo of food → backend `/cal` returns parsed nutrients
4. Scan a barcode → product card shows
5. Custom food entry → save → appears on Home today's list
6. Open Profile → BMI label matches body weight/height (CZ + EN locale)
7. Notification permission → schedule meal reminder → fires after the configured delay
