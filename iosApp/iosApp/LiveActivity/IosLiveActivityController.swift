import ActivityKit
import Foundation
import shared

final class IosLiveActivityController: LiveActivityController {
    private var activities: [Int64: Activity<FoodAnalysisAttributes>] = [:]

    func startFoodAnalysis(id: Int64) {
        Task { @MainActor in
            guard ActivityAuthorizationInfo().areActivitiesEnabled else { return }
            let attributes = FoodAnalysisAttributes(foodId: id)
            let state = FoodAnalysisAttributes.ContentState(status: .analyzing, foodName: "", calories: 0)
            let activity = try? Activity.request(
                attributes: attributes,
                content: ActivityContent(state: state, staleDate: nil)
            )
            self.activities[id] = activity
        }
    }

    func completeFoodAnalysis(id: Int64, name: String, calories: Int32) {
        let state = FoodAnalysisAttributes.ContentState(status: .done, foodName: name, calories: Int(calories))
        Task { @MainActor in
            guard let activity = self.activities[id] else { return }
            await activity.update(ActivityContent(state: state, staleDate: nil))
            await activity.end(
                ActivityContent(state: state, staleDate: nil),
                dismissalPolicy: .after(.now + 5)
            )
            self.activities[id] = nil
        }
    }

    func failFoodAnalysis(id: Int64) {
        let state = FoodAnalysisAttributes.ContentState(status: .failed, foodName: "", calories: 0)
        Task { @MainActor in
            guard let activity = self.activities[id] else { return }
            await activity.end(
                ActivityContent(state: state, staleDate: nil),
                dismissalPolicy: .after(.now + 2)
            )
            self.activities[id] = nil
        }
    }
}
