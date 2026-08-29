import ActivityKit

public enum AnalysisStatus: String, Codable, Hashable {
    case analyzing
    case done
    case failed
}

struct FoodAnalysisAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        public var status: AnalysisStatus
        public var foodName: String
        public var calories: Int

        public init(status: AnalysisStatus, foodName: String, calories: Int) {
            self.status = status
            self.foodName = foodName
            self.calories = calories
        }
    }

    public var foodId: Int64

    public init(foodId: Int64) {
        self.foodId = foodId
    }
}
