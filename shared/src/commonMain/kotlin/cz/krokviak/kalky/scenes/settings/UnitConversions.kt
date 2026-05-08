package cz.krokviak.kalky.scenes.settings

import cz.krokviak.kalky.core.common.UnitSystem
import cz.krokviak.kalky.core.common.formatFloat1
import kotlin.math.roundToInt

private const val LB_PER_KG = 2.2046226218f
private const val CM_PER_IN = 2.54f

fun weightUnitLabel(unitSystem: UnitSystem): String {
    return if (unitSystem == UnitSystem.METRIC) "kg" else "lb"
}

fun heightUnitLabel(unitSystem: UnitSystem): String {
    return if (unitSystem == UnitSystem.METRIC) "cm" else "in"
}

fun formatWeightForDisplay(metricKg: Float, unitSystem: UnitSystem): String {
    return if (unitSystem == UnitSystem.METRIC) {
        formatFloat1(metricKg)
    } else {
        formatFloat1(metricKg * LB_PER_KG)
    }
}

fun formatHeightForDisplay(metricCm: Float, unitSystem: UnitSystem): String {
    return if (unitSystem == UnitSystem.METRIC) {
        metricCm.roundToInt().toString()
    } else {
        formatFloat1(metricCm / CM_PER_IN)
    }
}

internal fun convertKgToLb(value: Float): Float = value * LB_PER_KG

internal fun convertLbToKg(value: Float): Float = value / LB_PER_KG

internal fun convertCmToIn(value: Float): Float = value / CM_PER_IN

internal fun convertInToCm(value: Float): Float = value * CM_PER_IN
