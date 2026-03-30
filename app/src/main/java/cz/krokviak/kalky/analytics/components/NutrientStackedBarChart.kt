package cz.krokviak.kalky.analytics.components

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.multiplatform.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.multiplatform.cartesian.data.columnSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.CartesianLayerPadding
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.multiplatform.common.component.LineComponent
import com.patrykandpatrick.vico.multiplatform.common.component.rememberLineComponent
import com.patrykandpatrick.vico.multiplatform.common.component.rememberTextComponent
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.multiplatform.common.data.ExtraStore
import com.patrykandpatrick.vico.multiplatform.common.fill
import com.patrykandpatrick.vico.multiplatform.common.shape.CorneredShape
import com.patrykandpatrick.vico.multiplatform.common.shape.Shape
import cz.krokviak.kalky.R
import cz.krokviak.kalky.analytics.CaloriesBar
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.ui.LocalDimensions
import cz.krokviak.kalky.ui.components.KalkyCard
import cz.krokviak.kalky.i18n.LocalStrings
import cz.krokviak.kalky.ui.components.KalkySegmentedControl

private enum class NutrientTab {
    CALORIES, PROTEIN, CARBS, FAT
}

@Composable
fun NutrientCalorieCard(
    modifier: Modifier = Modifier,
    bars: List<CaloriesBar>
) {
    val s = LocalStrings.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = NutrientTab.entries
    val tabLabels = listOf(s.common.calories, s.common.protein, s.common.carbs, s.common.fat)

    val dims = LocalDimensions.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dims.itemSpacing)
    ) {
        KalkySegmentedControl(
            selectedIndex = selectedTab,
            items = tabLabels,
            onItemSelected = { selectedTab = it },
            modifier = Modifier.fillMaxWidth()
        )

        KalkyCard(
            shape = RoundedCornerShape(32.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
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
                    .height(LocalDimensions.current.chartHeightLarge)
            ) {
                if (bars.isEmpty()) {
                    EmptyNutrientCard()
                } else {
                    val tab = tabs[selectedTab]
                    if (tab == NutrientTab.CALORIES) {
                        StackedCaloriesChart(bars = bars)
                    } else {
                        SingleNutrientChart(bars = bars, tab = tab)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyNutrientCard() {
    val dims = LocalDimensions.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dims.cardPadding),
        verticalArrangement = Arrangement.spacedBy(dims.halfSpacing)
    ) {
        Text(
            text = LocalStrings.current.analytics.noCaloriesTitle,
            color = AppTheme.colors.onBackground,
            fontWeight = FontWeight.ExtraBold,
            fontSize = dims.fontTitle
        )
        Text(
            text = LocalStrings.current.analytics.noCaloriesSubtitle,
            color = AppTheme.colors.onBackground,
            fontSize = dims.fontBody,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// Calories tab: stacked bars (protein + carbs + fat)
@Composable
private fun StackedCaloriesChart(bars: List<CaloriesBar>) {
    val dims = LocalDimensions.current
    val avgCalories = bars.sumOf { it.totalCalories } / bars.size

    Row(
        modifier = Modifier.fillMaxWidth().padding(dims.cardPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = LocalStrings.current.analytics.avgDailyIntake, color = AppTheme.colors.onBackground, fontSize = dims.fontBody, fontWeight = FontWeight.Bold)
        Text(text = "$avgCalories kcal", color = AppTheme.colors.onBackground, fontSize = dims.fontSubtitle, fontWeight = FontWeight.ExtraBold)
    }

    val proteinColor = colorResource(id = R.color.proteinColor)
    val carbColor = colorResource(id = R.color.carbsColor)
    val fatColor = colorResource(id = R.color.fatColor)

    val days = bars.map { it.label }
    val dayFormatter = CartesianValueFormatter { _, value, _ ->
        days.getOrNull(value.toInt()) ?: " "
    }

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(bars) {
        modelProducer.runTransaction {
            columnSeries {
                series(days.indices.toList(), bars.map { it.carbs.toDouble() * 4 })
                series(days.indices.toList(), bars.map { it.fat.toDouble() * 9 })
                series(days.indices.toList(), bars.map { it.protein.toDouble() * 4 })
            }
        }
    }

    val topColumn = rememberLineComponent(
        fill = fill(proteinColor),
        thickness = 20.dp,
        shape = CorneredShape.rounded(topLeftPercent = 40, topRightPercent = 40),
    )
    val middleColumn = rememberLineComponent(
        fill = fill(fatColor),
        thickness = 20.dp,
        shape = Shape.Rectangle,
    )
    val bottomColumn = rememberLineComponent(
        fill = fill(carbColor),
        thickness = 20.dp,
        shape = CorneredShape.rounded(bottomLeftPercent = 40, bottomRightPercent = 40),
    )

    val columnProvider = remember(topColumn, middleColumn, bottomColumn) {
        stackedColumnProvider(topColumn, middleColumn, bottomColumn)
    }

    val axisLabel = rememberTextComponent(
        style = TextStyle(color = AppTheme.colors.onBackground, fontSize = dims.fontCaption)
    )

    Row(modifier = Modifier.fillMaxSize().padding(dims.cardPadding)) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = columnProvider,
                    columnCollectionSpacing = dims.itemSpacing,
                    mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },
                ),
                startAxis = VerticalAxis.rememberStart(
                    label = axisLabel,
                    valueFormatter = CartesianValueFormatter { _, value, _ ->
                        "${value.toInt()} kcal"
                    },
                    itemPlacer = VerticalAxis.ItemPlacer.count({ 5 }),
                    guideline = null, line = null, tick = null
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    label = axisLabel,
                    itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned() },
                    guideline = null, line = null, tick = null,
                    valueFormatter = dayFormatter
                ),
                layerPadding = { CartesianLayerPadding(scalableStart = dims.cardPadding, scalableEnd = dims.cardPadding) },
            ),
            modelProducer = modelProducer,
            modifier = Modifier.fillMaxSize(),
            zoomState = rememberVicoZoomState(zoomEnabled = false),
        )
    }
}

// Protein/Carbs/Fat tabs: single-color bars
@Composable
private fun SingleNutrientChart(bars: List<CaloriesBar>, tab: NutrientTab) {
    val dims = LocalDimensions.current
    val values = bars.map { bar ->
        when (tab) {
            NutrientTab.PROTEIN -> bar.protein.toDouble()
            NutrientTab.CARBS -> bar.carbs.toDouble()
            NutrientTab.FAT -> bar.fat.toDouble()
            else -> 0.0
        }
    }
    val avg = if (values.isNotEmpty()) values.average().toInt() else 0

    val barColor = when (tab) {
        NutrientTab.PROTEIN -> colorResource(id = R.color.proteinColor)
        NutrientTab.CARBS -> colorResource(id = R.color.carbsColor)
        NutrientTab.FAT -> colorResource(id = R.color.fatColor)
        else -> AppTheme.colors.onBackground
    }
    val analyticsStrings = LocalStrings.current.analytics
    val headerTitle = when (tab) {
        NutrientTab.PROTEIN -> analyticsStrings.avgProtein
        NutrientTab.CARBS -> analyticsStrings.avgCarbs
        NutrientTab.FAT -> analyticsStrings.avgFat
        else -> ""
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(dims.cardPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = headerTitle, color = AppTheme.colors.onBackground, fontSize = dims.fontBody, fontWeight = FontWeight.Bold)
        Text(text = "$avg g", color = AppTheme.colors.onBackground, fontSize = dims.fontSubtitle, fontWeight = FontWeight.ExtraBold)
    }

    val days = bars.map { it.label }
    val dayFormatter = CartesianValueFormatter { _, value, _ ->
        days.getOrNull(value.toInt()) ?: " "
    }

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(bars, tab) {
        modelProducer.runTransaction {
            columnSeries {
                series(days.indices.toList(), values)
            }
        }
    }

    val column = rememberLineComponent(
        fill = fill(barColor),
        thickness = 20.dp,
        shape = CorneredShape.rounded(allPercent = 40),
    )

    val axisLabel = rememberTextComponent(
        style = TextStyle(color = AppTheme.colors.onBackground, fontSize = dims.fontCaption)
    )

    Row(modifier = Modifier.fillMaxSize().padding(dims.cardPadding)) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = remember(column) {
                        ColumnCartesianLayer.ColumnProvider.series(column)
                    },
                    columnCollectionSpacing = dims.itemSpacing,
                    rangeProvider = remember {
                        object : CartesianLayerRangeProvider {
                            override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
                                val range = maxY - minY
                                return if (range < 1.0) minY - 1.0 else (minY - range * 0.1).coerceAtLeast(0.0)
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
                        "${value.toInt()} g"
                    },
                    itemPlacer = VerticalAxis.ItemPlacer.count({ 5 }),
                    guideline = null, line = null, tick = null
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    label = axisLabel,
                    itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned() },
                    guideline = null, line = null, tick = null,
                    valueFormatter = dayFormatter
                ),
                layerPadding = { CartesianLayerPadding(scalableStart = dims.cardPadding, scalableEnd = dims.cardPadding) },
            ),
            modelProducer = modelProducer,
            modifier = Modifier.fillMaxSize(),
            zoomState = rememberVicoZoomState(zoomEnabled = false),
        )
    }
}

private fun stackedColumnProvider(
    top: LineComponent,
    middle: LineComponent,
    bottom: LineComponent
) = object : ColumnCartesianLayer.ColumnProvider {
    override fun getColumn(
        entry: ColumnCartesianLayerModel.Entry,
        seriesIndex: Int,
        extraStore: ExtraStore,
    ) = when (seriesIndex) {
        0 -> bottom
        1 -> middle
        else -> top
    }

    override fun getWidestSeriesColumn(seriesIndex: Int, extraStore: ExtraStore) =
        when (seriesIndex) {
            0 -> bottom
            1 -> middle
            else -> top
        }
}
