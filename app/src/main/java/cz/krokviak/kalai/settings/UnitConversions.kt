package cz.krokviak.kalai.settings

import java.util.Locale
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
        String.format(Locale.US, "%.1f", metricKg)
    } else {
        String.format(Locale.US, "%.1f", metricKg * LB_PER_KG)
    }
}

fun formatHeightForDisplay(metricCm: Float, unitSystem: UnitSystem): String {
    return if (unitSystem == UnitSystem.METRIC) {
        metricCm.roundToInt().toString()
    } else {
        String.format(Locale.US, "%.1f", metricCm / CM_PER_IN)
    }
}

internal fun convertKgToLb(value: Float): Float = value * LB_PER_KG

internal fun convertLbToKg(value: Float): Float = value / LB_PER_KG

internal fun convertCmToIn(value: Float): Float = value / CM_PER_IN

internal fun convertInToCm(value: Float): Float = value * CM_PER_IN
