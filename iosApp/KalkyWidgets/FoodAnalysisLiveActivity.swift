import ActivityKit
import SwiftUI
import WidgetKit

struct FoodAnalysisLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: FoodAnalysisAttributes.self) { context in
            LockScreenView(state: context.state)
                .widgetURL(deepLink(context.attributes.foodId))
        } dynamicIsland: { context in
            DynamicIsland {
                DynamicIslandExpandedRegion(.leading) {
                    Image(systemName: icon(context.state.status))
                        .font(.title2)
                        .foregroundStyle(.orange)
                }
                DynamicIslandExpandedRegion(.center) {
                    VStack(spacing: 2) {
                        Text(title(context.state))
                            .font(.headline)
                            .lineLimit(1)
                        if context.state.status == .done {
                            Text("\(context.state.calories) kcal")
                                .font(.subheadline)
                                .foregroundStyle(.secondary)
                        }
                    }
                }
            } compactLeading: {
                Image(systemName: icon(context.state.status))
                    .foregroundStyle(.orange)
            } compactTrailing: {
                trailing(context.state)
            } minimal: {
                Image(systemName: icon(context.state.status))
                    .foregroundStyle(.orange)
            }
            .widgetURL(deepLink(context.attributes.foodId))
        }
    }

    private func deepLink(_ id: Int64) -> URL? {
        URL(string: "kalky://food/\(id)")
    }

    private func icon(_ status: AnalysisStatus) -> String {
        switch status {
        case .analyzing: return "fork.knife"
        case .done: return "flame.fill"
        case .failed: return "exclamationmark.triangle.fill"
        }
    }

    private func title(_ state: FoodAnalysisAttributes.ContentState) -> String {
        switch state.status {
        case .analyzing: return "Analyzuji jídlo…"
        case .done: return state.foodName.isEmpty ? "Hotovo" : state.foodName
        case .failed: return "Nerozpoznáno"
        }
    }

    @ViewBuilder
    private func trailing(_ state: FoodAnalysisAttributes.ContentState) -> some View {
        switch state.status {
        case .analyzing:
            ProgressView().tint(.orange)
        case .done:
            Text("\(state.calories)")
                .font(.caption2)
                .bold()
        case .failed:
            Image(systemName: "xmark").foregroundStyle(.red)
        }
    }
}

struct LockScreenView: View {
    let state: FoodAnalysisAttributes.ContentState

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: state.status == .done ? "flame.fill" : "fork.knife")
                .font(.title)
                .foregroundStyle(.orange)
            VStack(alignment: .leading, spacing: 2) {
                Text(headline)
                    .font(.headline)
                    .lineLimit(1)
                subtitle
            }
            Spacer()
            if state.status == .analyzing {
                ProgressView().tint(.orange)
            }
        }
        .padding()
    }

    private var headline: String {
        switch state.status {
        case .analyzing: return "Analyzuji jídlo…"
        case .done: return state.foodName.isEmpty ? "Hotovo" : state.foodName
        case .failed: return "Nerozpoznáno"
        }
    }

    @ViewBuilder
    private var subtitle: some View {
        switch state.status {
        case .analyzing:
            Text("Kalky analyzuje fotku")
                .font(.subheadline)
                .foregroundStyle(.secondary)
        case .done:
            Text("\(state.calories) kcal")
                .font(.subheadline)
                .foregroundStyle(.secondary)
        case .failed:
            Text("Zkus to prosím znovu")
                .font(.subheadline)
                .foregroundStyle(.secondary)
        }
    }
}
