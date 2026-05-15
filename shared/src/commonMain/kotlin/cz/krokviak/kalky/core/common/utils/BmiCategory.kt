package cz.krokviak.kalky.core.common.utils

enum class BmiCategory {
    UNDERWEIGHT,
    NORMAL,
    OVERWEIGHT,
    OBESE,
}

fun bmiCategory(bmi: Float): BmiCategory = when {
    bmi < 18.5f -> BmiCategory.UNDERWEIGHT
    bmi < 25f -> BmiCategory.NORMAL
    bmi < 30f -> BmiCategory.OVERWEIGHT
    else -> BmiCategory.OBESE
}
