package cz.krokviak.kalai.analytics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.R
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.section.CupertinoSection


@Composable
fun MacroNutrientLegendRowOnly(
    proteinPercent: Float,
    carbsPercent: Float,
    fatPercent: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(
            label = "Bílkoviny",
            percent = proteinPercent,
            colorId = R.color.proteinColor
        )
        LegendItem(
            label = "Sacharidy",
            percent = carbsPercent,
            colorId = R.color.carbsColor
        )
        LegendItem(
            label = "Tuky",
            percent = fatPercent,
            colorId = R.color.fatColor
        )
    }

}

// Pomocná funkce pro vykreslení jednoho "kolečka" legendy
@Composable
private fun LegendItem(
    label: String,
    percent: Float,
    colorId: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(colorResource(id = colorId))
            )
            Spacer(modifier = Modifier.width(6.dp))
            CupertinoText(
                text = String.format("%.0f%%", percent),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        CupertinoText(
            text = label,
            fontSize = 12.sp
        )
    }
}
