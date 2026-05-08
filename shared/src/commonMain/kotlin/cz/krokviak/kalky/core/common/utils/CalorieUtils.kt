package cz.krokviak.kalky.core.common.utils

private const val KCAL_PER_GRAM_PROTEIN = 4
private const val KCAL_PER_GRAM_CARB = 4
private const val KCAL_PER_GRAM_FAT = 9

fun caloriesFromMacros(protein: Int, carbs: Int, fat: Int): Int =
    protein * KCAL_PER_GRAM_PROTEIN + carbs * KCAL_PER_GRAM_CARB + fat * KCAL_PER_GRAM_FAT

fun caloriesFromMacros(protein: Double, carbs: Double, fat: Double): Double =
    protein * KCAL_PER_GRAM_PROTEIN + carbs * KCAL_PER_GRAM_CARB + fat * KCAL_PER_GRAM_FAT
