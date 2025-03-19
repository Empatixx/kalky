package cz.krokviak.kalai.analytics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
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
import com.patrykandpatrick.vico.multiplatform.common.data.ExtraStore
import com.patrykandpatrick.vico.multiplatform.common.fill
import com.patrykandpatrick.vico.multiplatform.common.shape.CorneredShape
import com.patrykandpatrick.vico.multiplatform.common.shape.Shape
import cz.krokviak.kalai.R
import cz.krokviak.kalai.analytics.CaloriesBar
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

private val StartAxisItemPlacer = VerticalAxis.ItemPlacer.step({ 0.5 })

private fun getColumnProvider(
    top: LineComponent,
    middle: LineComponent,
    bottom: LineComponent
) = object : ColumnCartesianLayer.ColumnProvider {
    override fun getColumn(
        entry: ColumnCartesianLayerModel.Entry,
        seriesIndex: Int,
        extraStore: ExtraStore,
    ): LineComponent {
        return when (seriesIndex) {
            0 -> bottom       // First column: top
            1 -> middle    // Second column: middle
            else -> top // Any other series: bottom
        }
    }

    override fun getWidestSeriesColumn(seriesIndex: Int, extraStore: ExtraStore): LineComponent {
        return when (seriesIndex) {
            0 -> bottom       // First column: top
            1 -> middle    // Second column: middle
            else -> top // Any other series: bottom
        }
    }
}


@Composable
fun NutrientCalorieCard(
    modifier: Modifier = Modifier,
    bars: List<CaloriesBar>
) {
    MacroNutrientLegendRowOnly(
        proteinPercent = bars.sumBy { it.protein }.toFloat(),
        carbsPercent = bars.sumBy { it.carbs }.toFloat(),
        fatPercent = bars.sumBy { it.fat }.toFloat()
    )
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
        Column(modifier = Modifier.fillMaxWidth()
            .height(300.dp)
        ) {
            if (bars.isEmpty()) {
                EmptyNutrientCalorieCard()
            } else {
                NutrientCalorieCardInternal(
                    modifier = modifier,
                    bars = bars
                )
            }
        }
    }
}

@Composable
fun EmptyNutrientCalorieCard(){
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        CupertinoText(
            text = "Chybí informace o vaších kaloriích",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        CupertinoText(
            text = "Přidejte své kalorie v úvodu",
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}

@Composable
fun NutrientCalorieCardInternal(
    modifier: Modifier,
    bars: List<CaloriesBar>
){
    // Header with title and calorie summary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CupertinoText(
            text = "Průměrný denní příjem",
            color = Color.Black
        )
        CupertinoText(
            text = "2000 kcal",
            color = Color.Black
        )
    }
    // Graph row with Vico’s stacked column chart
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Retrieve nutrient colors (as defined in your resources)
        val proteinColor = colorResource(id = R.color.proteinColor)
        val carbColor = colorResource(id = R.color.carbsColor)
        val fatColor = colorResource(id = R.color.fatColor)
        val days = listOf("Po", "Út", "St", "Čt", "Pá", "So", "Ne")

        // Create a custom value formatter that maps numeric values to day names.
        val dayFormatter = CartesianValueFormatter { context, value, _ ->
            // Convert the value (typically a Double) to an integer index.
            val index = value.toInt()
            // Return the corresponding day name, or an empty string if out of bounds.
            days.getOrNull(index) ?: ""
        }
        // Create and populate the Vico chart’s model
        val modelProducer = remember { CartesianChartModelProducer() }
        LaunchedEffect(Unit) {
            modelProducer.runTransaction {
                columnSeries {
                    series(days.indices.toList(), listOf(30.0, 40.0, 20.0, 50.0, 40.0, 30.0, 20.0))
                    series(days.indices.toList(), listOf(50.0, 30.0, 40.0, 20.0, 30.0, 40.0, 50.0))
                    series(days.indices.toList(), listOf(20.0, 30.0, 40.0, 30.0, 30.0, 30.0, 30.0))
                }
            }
        }

        val topColumn =
            rememberLineComponent(
                fill = fill(proteinColor),
                thickness = 8.dp,
                shape = CorneredShape.rounded(topLeftPercent = 40, topRightPercent = 40),
            )
        val bottomColumn =
            rememberLineComponent(
                fill = fill(carbColor),
                thickness = 8.dp,
                shape = CorneredShape.rounded(bottomLeftPercent = 40, bottomRightPercent = 40),
            )
        val middleColumn =
            rememberLineComponent(
                fill = fill(fatColor),
                thickness = 8.dp,
                shape = Shape.Rectangle,
            )

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider =
                    remember(topColumn, middleColumn, middleColumn) {
                        getColumnProvider(topColumn, middleColumn, bottomColumn)
                    },
                    columnCollectionSpacing = 16.dp,
                    mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },
                ),
                startAxis = VerticalAxis.rememberStart(
                    valueFormatter = CartesianValueFormatter.decimal(suffix = " kcal"),
                    itemPlacer = StartAxisItemPlacer,
                    guideline = null,
                    line = null,
                    tick = null
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned() },
                    guideline = null,
                    line = null,
                    tick = null,
                    valueFormatter = dayFormatter
                ),
                layerPadding = { CartesianLayerPadding(scalableStart = 16.dp, scalableEnd = 16.dp) },
            ),
            modelProducer = modelProducer,
            modifier = Modifier.fillMaxSize(),
            zoomState = rememberVicoZoomState(zoomEnabled = false),
        )
    }
}

@Composable
fun NutrientRatioLegend(
    proteinPercent: Float,
    carbsPercent: Float,
    fatPercent: Float,
    modifier: Modifier = Modifier
) {
        Row(
            modifier = modifier.fillMaxWidth()
                .padding(bottom = 8.dp),
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
