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
)

val normalDimensions = Dimensions(
    screenPadding = 16.dp,
    cardPadding = 16.dp,
    itemSpacing = 16.dp,
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
)

val LocalDimensions = compositionLocalOf { normalDimensions }
