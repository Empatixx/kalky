package cz.krokviak.kalky.scenes.customfood

import cz.krokviak.kalky.scenes.barcode.data.OpenFoodFactsNutriments
import cz.krokviak.kalky.core.common.utils.caloriesFromMacros
import kotlin.math.roundToInt

data class ScaledNutrients(
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int
)

fun OpenFoodFactsNutriments?.scaledTo(grams: Int): ScaledNutrients {
    val factor = grams / 100.0
    val protein = ((this?.proteins100g ?: 0.0) * factor).roundToInt()
    val carbs = ((this?.carbohydrates100g ?: 0.0) * factor).roundToInt()
    val fat = ((this?.fat100g ?: 0.0) * factor).roundToInt()
    val rawCalories = ((this?.energyKcal100g ?: 0.0) * factor).roundToInt()
    val calories = if (rawCalories > 0) rawCalories else caloriesFromMacros(protein, carbs, fat)
    return ScaledNutrients(calories = calories, protein = protein, carbs = carbs, fat = fat)
}
