package cz.krokviak.kalky.core.common.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class BmiCategoryTest {

    @Test
    fun underweight_below_18_5() {
        assertEquals(BmiCategory.UNDERWEIGHT, bmiCategory(15f))
        assertEquals(BmiCategory.UNDERWEIGHT, bmiCategory(18.4f))
    }

    @Test
    fun normal_18_5_to_below_25() {
        assertEquals(BmiCategory.NORMAL, bmiCategory(18.5f))
        assertEquals(BmiCategory.NORMAL, bmiCategory(22f))
        assertEquals(BmiCategory.NORMAL, bmiCategory(24.9f))
    }

    @Test
    fun overweight_25_to_below_30() {
        assertEquals(BmiCategory.OVERWEIGHT, bmiCategory(25f))
        assertEquals(BmiCategory.OVERWEIGHT, bmiCategory(27.5f))
        assertEquals(BmiCategory.OVERWEIGHT, bmiCategory(29.9f))
    }

    @Test
    fun obese_30_or_above() {
        assertEquals(BmiCategory.OBESE, bmiCategory(30f))
        assertEquals(BmiCategory.OBESE, bmiCategory(45f))
    }
}
