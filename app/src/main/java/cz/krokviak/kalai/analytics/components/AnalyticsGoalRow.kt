package cz.krokviak.kalai.analytics.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.alexzhirkevich.cupertino.CupertinoText

@Composable
fun AnalyticsGoalRow(
    days: Int,
    achievedPercent: Float,
    weightChange: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 1) "Dosaženo cíle za X dní" a procenta
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CupertinoText(
                text = "Dosaženo cíle za $days dní",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            CupertinoText(
                text = "${achievedPercent.toInt()}%",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        // 2) Řádek se změnou váhy
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CupertinoText(
                text = "Změna váhy",
                color = Color.Black,
                fontSize = 14.sp
            )
            // Zde třeba v kg. Můžeš si zformátovat podle svého
            CupertinoText(
                text = "${weightChange} kg",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
