package cz.krokviak.kalky.common

import kotlin.math.roundToInt

fun formatFloat1(value: Float): String {
    val rounded = (value * 10).roundToInt()
    val whole = rounded / 10
    val frac = rounded % 10
    return if (frac < 0) "$whole.${-frac}" else "$whole.$frac"
}
