package cz.krokviak.kalai.analytics.components

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.section.CupertinoSection
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line

@Composable
fun WeightLineChart(
    weights: List<Double>,
    currentWeight: Double = weights.lastOrNull() ?: 0.0,
    modifier: Modifier = Modifier
) {
    CupertinoSection(
        shape = RoundedCornerShape(32.dp),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(32.dp)
            )
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .height(300.dp)
        ) {
            if (weights.isEmpty()) {
                EmptyWeightLineChart()
            } else {
                WeightLineChartInternal(
                    weights = weights,
                    currentWeight = currentWeight
                )
            }
        }
    }
}

@Composable
fun EmptyWeightLineChart() {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        CupertinoText(
            text = "Chybí informace o vaší váze",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        CupertinoText(
            text = "Přidejte svou váhu v nastavení",
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}

@Composable()
fun WeightLineChartInternal(
    weights: List<Double>,
    currentWeight: Double = weights.lastOrNull() ?: 0.0
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CupertinoText(
            text = "Váha",
            color = Color.Black
        )
        CupertinoText(
            text = currentWeight.format(1) + " kg",
            color = Color.Black
        )
    }

    // Graph row below the header
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
            LineChart(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                gridProperties = GridProperties(false),
                dividerProperties = DividerProperties(false),
                data = remember {
                    listOf(
                        Line(
                            values = weights,
                            color = SolidColor(Color.Black),
                            firstGradientFillColor = Color.Black.copy(alpha = .5f),
                            secondGradientFillColor = Color.Transparent,
                            strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                            gradientAnimationDelay = 1000,
                            drawStyle = DrawStyle.Stroke(width = 2.dp),
                            label = "Váha"
                        )
                    )
                },
                labelHelperProperties = LabelHelperProperties(false),
                labelProperties = LabelProperties(false),
                animationMode = AnimationMode.Together(delayBuilder = {
                    it * 500L
                }),
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = TextStyle.Default,
                    padding = 16.dp,
                    contentBuilder = { value ->
                        value.format(1) + " kg"
                    }
                ),
            )

    }
}