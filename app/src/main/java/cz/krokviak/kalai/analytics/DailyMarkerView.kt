package cz.krokviak.kalai.analytics

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import cz.krokviak.kalai.R
import cz.krokviak.kalai.home.DailyStats

class DailyMarkerView(
    context: Context,
    private val stats: List<DailyStats>
) : MarkerView(context, R.layout.marker_view) {

    // This is the TextView in marker_view.xml
    private val tvContent: TextView = findViewById(R.id.tvContent)

    // Called every time the marker is to be redrawn
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e is BarEntry) {
            val dayIndex = e.x.toInt()
            if (dayIndex in stats.indices) {
                val totalCal = stats[dayIndex].totalCalories
                // Update the text you want to display
                tvContent.text = "Celkem: $totalCal"
            }
        }
        super.refreshContent(e, highlight)
    }

    // Positions the marker above the selected bar
    override fun getOffset(): MPPointF {
        // Center the marker horizontally, place it above the bar
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}
