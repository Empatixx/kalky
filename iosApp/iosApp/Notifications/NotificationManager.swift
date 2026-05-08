import UserNotifications
import FirebaseMessaging
import UIKit

/// Manages push notifications (FCM) and local meal reminders on iOS.
class NotificationManager: NSObject, ObservableObject {
    static let shared = NotificationManager()

    @Published var isAuthorized = false

    override init() {
        super.init()
        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self
        checkAuthorizationStatus()
    }

    // MARK: - Permission

    func requestPermission() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { [weak self] granted, _ in
            DispatchQueue.main.async {
                self?.isAuthorized = granted
            }
            if granted {
                DispatchQueue.main.async {
                    UIApplication.shared.registerForRemoteNotifications()
                }
            }
        }
    }

    func checkAuthorizationStatus() {
        UNUserNotificationCenter.current().getNotificationSettings { [weak self] settings in
            DispatchQueue.main.async {
                self?.isAuthorized = settings.authorizationStatus == .authorized
            }
        }
    }

    // MARK: - Local Meal Reminders

    /// Schedule a local notification reminder to log food.
    func scheduleMealReminder(title: String, body: String, delayHours: Double = 3.0) {
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = body
        content.sound = .default

        let trigger = UNTimeIntervalNotificationTrigger(
            timeInterval: delayHours * 3600,
            repeats: false
        )

        let request = UNNotificationRequest(
            identifier: "meal_reminder_\(UUID().uuidString)",
            content: content,
            trigger: trigger
        )

        UNUserNotificationCenter.current().add(request)
    }

    /// Cancel all pending meal reminder notifications.
    func cancelMealReminders() {
        UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
    }
}

// MARK: - UNUserNotificationCenterDelegate

extension NotificationManager: UNUserNotificationCenterDelegate {
    /// Show notification even when app is in foreground.
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.banner, .badge, .sound])
    }

    /// Handle notification tap.
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        completionHandler()
    }
}

// MARK: - MessagingDelegate (FCM)

extension NotificationManager: MessagingDelegate {
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let token = fcmToken else { return }
        print("FCM token: \(token)")
        Task { await postTokenToBackend(token) }
    }

    /// POST /api/auth/fcm-token with the user's Firebase ID token + AppCheck token.
    /// Mirrors the Android KalkyFcmService.onNewToken() behavior.
    private func postTokenToBackend(_ fcmToken: String) async {
        let backendUrl = IosRemoteConfigManager.getBackendBaseUrl()
        guard let url = URL(string: "\(backendUrl)/api/auth/fcm-token") else { return }

        let idToken: String?
        do {
            idToken = try await IosAuthTokenProvider().getIdToken()
        } catch {
            print("FCM token sync skipped — no Firebase ID token: \(error)")
            return
        }
        guard let bearer = idToken else {
            // User not signed in yet — token will be retried on next refresh.
            return
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer \(bearer)", forHTTPHeaderField: "Authorization")

        // App Check token is best-effort; backend rejects without it in prod.
        if let appCheck = try? await IosAppCheckTokenProvider().getToken(), !appCheck.isEmpty {
            request.setValue(appCheck, forHTTPHeaderField: "X-Firebase-AppCheck")
        }

        let payload: [String: String] = ["token": fcmToken]
        guard let body = try? JSONSerialization.data(withJSONObject: payload) else { return }
        request.httpBody = body

        do {
            let (_, response) = try await URLSession.shared.data(for: request)
            if let http = response as? HTTPURLResponse, !(200..<300).contains(http.statusCode) {
                print("FCM token sync failed: HTTP \(http.statusCode)")
            }
        } catch {
            print("FCM token sync network error: \(error)")
        }
    }
}
