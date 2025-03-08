package cz.krokviak.kalai

import android.os.Bundle
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import cz.krokviak.kalai.camera.CameraActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen(
                    onCaptureClick = {
                        val intent = Intent(this,
                            CameraActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

/**
 * MainScreen sets up the overall UI: a Scaffold containing a bottom nav, a FAB, and screen content.
 */
@Composable
fun MainScreen(onCaptureClick: () -> Unit) {
    Scaffold(
        bottomBar = { MyBottomNavBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCaptureClick,
                containerColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier
                    .offset(y = 48.dp)
                    .size(64.dp),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        MyScreenContent(Modifier.padding(innerPadding))
    }
}
/**
 * MyScreenContent displays the main body of the screen:
 * - A card showing "Calories left" + a donut chart
 * - A row of nutrient cards
 */
@Composable
fun MyScreenContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val context = LocalContext.current
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            ),
        ) {
            Row(
                modifier = Modifier.padding(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: Text content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "1250",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Calories left")
                }

                // Right side: Donut chart with an overlaid icon
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DonutChart(
                        modifier = Modifier.fillMaxSize(),
                        percentage = 0.6f,            // 60% filled
                        activeColor = Color.Black,
                        centerIcon = Icons.Outlined.LocalFireDepartment,
                        centerIconSize = 32.dp,
                        holeRadius = 80f
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Row with three cards: Protein, Carbs, Fat
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            NutrientCard(
                amount = "45g",
                description = "Protein over",
                icon = ImageVector.vectorResource(R.drawable.meat_svgrepo_com),
                donutColor = colorResource(id = R.color.proteinColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            NutrientCard(
                amount = "89g",
                description = "Carbs left",
                icon = ImageVector.vectorResource(R.drawable.wheat),
                donutColor = colorResource(id = R.color.carbsColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            NutrientCard(
                amount = "48g",
                description = "Fat left",
                icon = ImageVector.vectorResource(R.drawable.avocado),
                donutColor = colorResource(id = R.color.fatColor)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Nedávno přidané",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )


    }
}

/**
 * A reusable card composable for displaying a nutrient’s info (Protein, Carbs, Fat).
 */
@Composable
fun RowScope.NutrientCard(
    amount: String,
    description: String,
    icon: ImageVector,
    donutColor: Color
) {
    val context = LocalContext.current
    OutlinedCard(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
    ) {
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 0.dp
            ),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = amount,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                fontSize = 12.sp
            )
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                DonutChart(
                    modifier = Modifier.fillMaxSize(),
                    percentage = 0.6f,
                    activeColor = donutColor,
                    centerIcon = icon,
                    centerIconSize = 24.dp,
                    holeRadius = 80f
                )
            }
        }
    }
}

/**
 * MyBottomNavBar sets up the bottom navigation with three items:
 * "Výchozí", "Analýza", and "Nastavení".
 */
@Composable
fun MyBottomNavBar() {
    var selectedItem by remember { mutableStateOf(0) }

    val items = listOf(
        Icons.Outlined.Home to "Výchozí",
        Icons.Outlined.Analytics to "Analýza",
        Icons.Outlined.Settings to "Nastavení"
    )

    NavigationBar(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Spacer to push items right (start padding).
        Spacer(modifier = Modifier.width(8.dp))

        items.forEachIndexed { index, (icon, label) ->
            val alphaValue = if (selectedItem == index) 1f else 0.5f
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .alpha(alphaValue)
                    )
                },
                label = {
                    Text(
                        text = label,
                        modifier = Modifier.alpha(alphaValue)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent // disables the highlight indicator
                )
            )
        }

        // Spacer with weight(1f) to consume leftover space,
        // keeping items left-aligned.
        Spacer(modifier = Modifier.weight(1f))

    }
}

/* -------------------------------------------------------------
   Reusable DonutChart Composable + Helper
   ------------------------------------------------------------- */

/**
 * DonutChart is a reusable composable that displays a PieChart from MPAndroidChart
 * with a hole in the middle and an optional icon overlay.
 *
 * @param modifier The [Modifier] to be applied to this layout.
 * @param percentage The percentage of the "active" portion (0.0 - 1.0).
 * @param activeColor The color for the active portion of the donut.
 * @param inactiveColor The color for the inactive portion of the donut (default = light gray).
 * @param holeRadius The hole radius in percentage of the chart’s radius. Default = 80% for a thick ring.
 * @param centerIcon Optional icon to overlay in the center of the chart.
 * @param centerIconSize Size of the center icon if provided.
 */
@Composable
fun DonutChart(
    modifier: Modifier = Modifier,
    percentage: Float,
    activeColor: Color,
    inactiveColor: Color = Color(0xFFBDBDBD),
    holeRadius: Float = 80f,
    centerIcon: ImageVector? = null,
    centerIconSize: Dp = 32.dp
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Donut Chart AndroidView
        AndroidView(
            factory = { context ->
                createPieChart(
                    context = context,
                    percentage = percentage,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    holeRadius = holeRadius
                )
            },
            modifier = Modifier.matchParentSize()
        )

        // Gray circle exactly around the icon, behind the icon but in front of donut
        if (centerIcon != null) {
            Box(
                modifier = Modifier
                    .size(centerIconSize + 16.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = centerIcon,
                    tint = activeColor,
                    contentDescription = null,
                    modifier = Modifier.size(centerIconSize)
                )
            }
        }
    }
}


/**
 * Creates and configures the underlying PieChart.
 * This function sets up colors, hides unnecessary labels/legends, etc.
 */
fun createPieChart(
    context: Context,
    percentage: Float,
    activeColor: Color,
    inactiveColor: Color,
    holeRadius: Float
): PieChart {
    return PieChart(context).apply {
        description.isEnabled = false
        setUsePercentValues(false)
        isDrawHoleEnabled = true
        setHoleColor(android.graphics.Color.TRANSPARENT)
        setHoleRadius(holeRadius)
        transparentCircleRadius = 0f
        isClickable = false
        isHighlightPerTapEnabled = false
        setTouchEnabled(false)
        setDrawEntryLabels(false)

        // Disable legend
        legend.isEnabled = false

        // Build data entries based on the percentage
        val entries = buildPieEntries(percentage)

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                activeColor.toArgb(),
                inactiveColor.toArgb()
            )
            // Hide value labels
            setDrawValues(false)
        }

        data = PieData(dataSet)
        invalidate()
    }
}

/**
 * Returns two [PieEntry] items:
 * 1. Active portion (percentage)
 * 2. Remainder portion (1f - percentage)
 */
fun buildPieEntries(percentage: Float): List<PieEntry> {
    val active = (percentage * 100).coerceIn(0f, 100f)
    val remainder = 100f - active
    return listOf(
        PieEntry(active, ""),     // active portion
        PieEntry(remainder, "")   // inactive portion
    )
}
