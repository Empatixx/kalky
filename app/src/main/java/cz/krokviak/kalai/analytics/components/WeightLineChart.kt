package cz.krokviak.kalai.analytics.components

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
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
    currentWeight: Double = weights.last(),
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
        ) {
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
                    text = "99.9 kg",
                    color = Color.Black
                )
            }

            // Graph row below the header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(300.dp)
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
                                values = listOf(28.0, 41.0, 5.0, 10.0, 35.0),
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
    }
}