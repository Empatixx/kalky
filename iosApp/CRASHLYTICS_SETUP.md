# iOS Crashlytics + Performance Setup

These two Firebase modules are present on Android (`firebase-crashlytics` + `firebase-perf` plugins in `app/build.gradle.kts`) but missing on iOS. This file is the recipe to add them.

## Why it can't be automated

iOS Firebase deps live in `iosApp/iosApp.xcodeproj/project.pbxproj` (Swift Package Manager refs). Editing that file safely needs Xcode itself — automated edits frequently drift the project file format and break the build.

The Swift code that *uses* Crashlytics is already committed (`iosApp/iosApp/Crashlytics/CrashlyticsManager.swift`, gated with `#if canImport(FirebaseCrashlytics)`). It compiles to a no-op until you add the SPM dependency below; once you add it, the user-ID tracking turns on automatically.

## Steps (do these in Xcode on macOS)

### 1. Add Swift Package dependencies

1. Open `iosApp/iosApp.xcodeproj` in Xcode.
2. **File → Add Package Dependencies…**
3. URL: `https://github.com/firebase/firebase-ios-sdk`
4. Version: **Up to next major version** (the project already pins to a Firebase SDK version via the existing imports — match it).
5. Check these products:
    - `FirebaseCrashlytics`
    - `FirebasePerformance`
6. Add to target **iosApp**.

### 2. Crashlytics build phase

Crashlytics needs a Run Script Phase to upload dSYMs (otherwise stack traces stay obfuscated):

1. Select the **iosApp** target → **Build Phases**.
2. Click **+** → **New Run Script Phase**.
3. Script:
    ```sh
    "${BUILD_DIR%Build/*}SourcePackages/checkouts/firebase-ios-sdk/Crashlytics/run"
    ```
4. Input Files (one per line):
    ```
    ${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}/Contents/Resources/DWARF/${TARGET_NAME}
    $(SRCROOT)/$(BUILT_PRODUCTS_DIR)/$(INFOPLIST_PATH)
    ```
5. Make sure the script phase runs **after** "Embed Frameworks".

### 3. Initialize Performance Monitoring

`FirebasePerformance` auto-instruments network calls and app starts once imported. In `iOSApp.swift`, after `FirebaseApp.configure()`:

```swift
import FirebaseCore
import FirebasePerformance  // <- add this

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        // Performance auto-starts on first import in release builds.
        // Optional: disable in debug to keep the dashboard clean.
        #if DEBUG
        Performance.sharedInstance().isInstrumentationEnabled = false
        #endif
    }
}
```

### 4. (Optional) Force a test crash

Add a debug menu item that calls:
```swift
fatalError("Crashlytics test crash")
```
Run on a real device, relaunch — the crash should appear in the Crashlytics dashboard within minutes.

### 5. Wire dSYM upload to CI

If you build releases via `xcodebuild` in CI:

```yaml
- name: Upload dSYMs
  run: |
    find $DERIVED_DATA -name "*.dSYM" -exec \
      "${SourcePackages}/checkouts/firebase-ios-sdk/Crashlytics/upload-symbols" \
      -gsp "$GSP_PATH" -p ios {} \;
```

`upload-symbols` ships with the SDK at the path used in step 2.

## What turns on automatically

Once the SPM dependencies are added:

- **`CrashlyticsManager.setUserId(uid)`** in `IosAuthStateProvider.swift` — tags every crash with the user's Firebase UID, mirroring Android.
- **Performance auto-instrumentation** — network requests (URLSession), app start, screen rendering, all visible in the Firebase console.

## Recipe verification

After completing the steps:
1. Build in **Release** scheme.
2. Run on a real device (Crashlytics doesn't ingest from Simulator).
3. Sign in → `Crashlytics.crashlytics().setUserID(uid)` fires.
4. Force a crash → wait ~1 minute → check Firebase console under **Crashlytics → Crash-free users**.
5. Check **Performance → Network requests** — should show traces for `/cal`, `/api/auth/me`, etc.

## Why no `Firebase.crashlytics.setUserId("")` mirror at sign-out

Crashlytics treats "" as a valid user ID equally with any other. We follow the Android pattern exactly: empty string clears the field for the next session. If you'd prefer to use `nil` semantics, switch to `setCustomKeysAndValues(["uid": NSNull()])` — purely cosmetic in the dashboard.
