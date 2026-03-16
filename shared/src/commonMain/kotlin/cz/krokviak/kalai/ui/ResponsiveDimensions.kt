package cz.krokviak.kalai.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ScreenSizeClass {
    Compact,  // < 360dp
    Normal,   // 360–411dp
    Large     // > 411dp
}

data class Dimensions(
    val screenPadding: Dp,
    val cardPadding: Dp,
    val itemSpacing: Dp,
    val sectionSpacing: Dp,
    val chartHeight: Dp,
    val chartHeightLarge: Dp,
    val buttonHeight: Dp,
    val buttonPaddingH: Dp,
    val buttonPaddingV: Dp,
    val fontTitle: TextUnit,
    val fontBody: TextUnit,
    val fontCaption: TextUnit,
    val contextMenuMinWidth: Dp,
    val iconSize: Dp,
    val fontHero: TextUnit,
    val fontSubtitle: TextUnit,
    val fontSmall: TextUnit,
    val rowHeight: Dp,
    val iconCircleSize: Dp,
    val thumbnailSize: Dp,
    val donutChartSize: Dp,
    val halfSpacing: Dp,
)

val compactDimensions = Dimensions(
    screenPadding = 8.dp,
    cardPadding = 8.dp,
    itemSpacing = 8.dp,
    sectionSpacing = 8.dp,
    chartHeight = 200.dp,
    chartHeightLarge = 260.dp,
    buttonHeight = 32.dp,
    buttonPaddingH = 10.dp,
    buttonPaddingV = 6.dp,
    fontTitle = 18.sp,
    fontBody = 13.sp,
    fontCaption = 10.sp,
    contextMenuMinWidth = 130.dp,
    iconSize = 18.dp,
    fontHero = 28.sp,
    fontSubtitle = 16.sp,
    fontSmall = 12.sp,
    rowHeight = 44.dp,
    iconCircleSize = 32.dp,
    thumbnailSize = 100.dp,
    donutChartSize = 90.dp,
    halfSpacing = 4.dp,
)

val normalDimensions = Dimensions(
    screenPadding = 12.dp,
    cardPadding = 16.dp,
    itemSpacing = 12.dp,
    sectionSpacing = 16.dp,
    chartHeight = 300.dp,
    chartHeightLarge = 400.dp,
    buttonHeight = 40.dp,
    buttonPaddingH = 16.dp,
    buttonPaddingV = 12.dp,
    fontTitle = 24.sp,
    fontBody = 16.sp,
    fontCaption = 12.sp,
    contextMenuMinWidth = 160.dp,
    iconSize = 24.dp,
    fontHero = 36.sp,
    fontSubtitle = 20.sp,
    fontSmall = 15.sp,
    rowHeight = 56.dp,
    iconCircleSize = 40.dp,
    thumbnailSize = 125.dp,
    donutChartSize = 105.dp,
    halfSpacing = 8.dp,
)

val largeDimensions = Dimensions(
    screenPadding = 20.dp,
    cardPadding = 20.dp,
    itemSpacing = 20.dp,
    sectionSpacing = 20.dp,
    chartHeight = 340.dp,
    chartHeightLarge = 440.dp,
    buttonHeight = 44.dp,
    buttonPaddingH = 20.dp,
    buttonPaddingV = 14.dp,
    fontTitle = 28.sp,
    fontBody = 17.sp,
    fontCaption = 13.sp,
    contextMenuMinWidth = 180.dp,
    iconSize = 28.dp,
    fontHero = 40.sp,
    fontSubtitle = 22.sp,
    fontSmall = 16.sp,
    rowHeight = 62.dp,
    iconCircleSize = 44.dp,
    thumbnailSize = 140.dp,
    donutChartSize = 140.dp,
    halfSpacing = 10.dp,
)

val LocalDimensions = compositionLocalOf { normalDimensions }
