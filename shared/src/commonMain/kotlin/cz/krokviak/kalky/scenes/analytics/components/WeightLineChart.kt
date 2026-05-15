package cz.krokviak.kalky.scenes.analytics.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.CartesianLayerPadding
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.multiplatform.common.component.rememberTextComponent
import com.patrykandpatrick.vico.multiplatform.common.data.ExtraStore
import com.patrykandpatrick.vico.multiplatform.common.fill
import cz.krokviak.kalky.core.common.formatFloat1
import cz.krokviak.kalky.core.common.repo.WeightEntry
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.core.ui.LocalDimensions
import cz.krokviak.kalky.core.ui.components.KalkyCard

@Composable
fun WeightLineChart(
    weights: List<WeightEntry>,
    modifier: Modifier = Modifier
) {
    KalkyCard(
        shape = RoundedCornerShape(32.dp),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .border(
                width = 1.dp,
                color = AppTheme.colors.border,
                shape = RoundedCornerShape(32.dp)
            )
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalDimensions.current.chartHeight)
        ) {
            if (weights.isEmpty()) {
                EmptyWeightLineChart()
            } else {
                WeightLineChartInternal(weights = weights)
            }
        }
    }
}

@Composable
private fun EmptyWeightLineChart() {
    val dims = LocalDimensions.current
    val s = LocalStrings.current.analytics
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dims.cardPadding),
        verticalArrangement = Arrangement.spacedBy(dims.halfSpacing)
    ) {
        Text(
            text = s.weightEmptyTitle,
            color = AppTheme.colors.onBackground,
            fontWeight = FontWeight.ExtraBold,
            fontSize = dims.fontTitle
        )
        Text(
            text = s.weightEmptySubtitle,
            color = AppTheme.colors.onBackground,
            fontSize = dims.fontBody,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun WeightLineChartInternal(weights: List<WeightEntry>) {
    val dims = LocalDimensions.current
    val s = LocalStrings.current.analytics
    val currentWeight = remember(weights) { weights.last().weight }
    val avgWeight = remember(weights) { weights.map { it.weight }.average() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dims.cardPadding, vertical = dims.halfSpacing * 1.5f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = s.weightCurrent,
                color = AppTheme.colors.onBackgroundSecondary,
                fontSize = dims.fontBody,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${formatFloat1(currentWeight.toFloat())} kg",
                color = AppTheme.colors.onBackground,
                fontWeight = FontWeight.ExtraBold,
                fontSize = dims.fontTitle
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = s.weightAverage,
                color = AppTheme.colors.onBackgroundSecondary,
                fontSize = dims.fontBody,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${formatFloat1(avgWeight.toFloat())} kg",
                color = AppTheme.colors.onBackground,
                fontWeight = FontWeight.ExtraBold,
                fontSize = dims.fontTitle
            )
        }
    }

    val dateLabels = remember(weights) {
        weights.map { entry ->
            "${entry.date.dayOfMonth}.${entry.date.monthNumber}."
        }
    }

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(weights) {
        modelProducer.runTransaction {
            lineSeries {
                series(weights.indices.toList(), weights.map { it.weight })
            }
        }
    }

    val dateFormatter = CartesianValueFormatter { _, value, _ ->
        dateLabels.getOrNull(value.toInt()) ?: " "
    }

    val line = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(fill(AppTheme.colors.chartLine)),
        areaFill = LineCartesianLayer.AreaFill.single(fill(AppTheme.colors.chartAreaFill)),
    )

    val axisLabel = rememberTextComponent(
        style = TextStyle(color = AppTheme.colors.onBackground, fontSize = dims.fontCaption)
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(dims.cardPadding)
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider = LineCartesianLayer.LineProvider.series(line),
                    rangeProvider = remember {
                        object : CartesianLayerRangeProvider {
                            override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
                                val range = maxY - minY
                                return if (range < 1.0) minY - 1.0 else minY - range * 0.1
                            }
                            override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
                                val range = maxY - minY
                                return if (range < 1.0) maxY + 1.0 else maxY + range * 0.1
                            }
                        }
                    }
                ),
                startAxis = VerticalAxis.rememberStart(
                    label = axisLabel,
                    valueFormatter = CartesianValueFormatter { _, value, _ ->
                        "${formatFloat1(value.toFloat())} kg"
                    },
                    itemPlacer = VerticalAxis.ItemPlacer.count({ 5 }),
                    guideline = null,
                    line = null,
                    tick = null
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    label = axisLabel,
                    itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned() },
                    guideline = null,
                    line = null,
                    tick = null,
                    valueFormatter = dateFormatter
                ),
                layerPadding = { CartesianLayerPadding(scalableStart = dims.cardPadding, scalableEnd = dims.cardPadding) },
            ),
            modelProducer = modelProducer,
            modifier = Modifier.fillMaxSize(),
            zoomState = rememberVicoZoomState(zoomEnabled = false),
        )
    }
}
