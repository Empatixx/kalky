package cz.krokviak.kalky.scenes.customfood

import cz.krokviak.kalky.scenes.barcode.data.OpenFoodFactsNutriments
import kotlin.test.Test
import kotlin.test.assertEquals

class PortionScalingTest {

    @Test
    fun scaledTo_nullNutriments_returnsZeros() {
        val scaled = (null as OpenFoodFactsNutriments?).scaledTo(150)
        assertEquals(0, scaled.calories)
        assertEquals(0, scaled.protein)
        assertEquals(0, scaled.carbs)
        assertEquals(0, scaled.fat)
    }

    @Test
    fun scaledTo_scalesValuesProportionallyTo100g() {
        val nutriments = OpenFoodFactsNutriments(
            energyKcal100g = 200.0,
            proteins100g = 10.0,
            fat100g = 5.0,
            carbohydrates100g = 25.0,
        )
        val scaled = nutriments.scaledTo(150)
        // 150g portion: 200*1.5=300, 10*1.5=15, 5*1.5=7.5→8, 25*1.5=37.5→38
        assertEquals(300, scaled.calories)
        assertEquals(15, scaled.protein)
        assertEquals(8, scaled.fat)
        assertEquals(38, scaled.carbs)
    }

    @Test
    fun scaledTo_fallsBackToMacroCaloriesWhenEnergyMissing() {
        val nutriments = OpenFoodFactsNutriments(
            energyKcal100g = null,
            proteins100g = 10.0,
            fat100g = 5.0,
            carbohydrates100g = 20.0,
        )
        val scaled = nutriments.scaledTo(100)
        // Falls back to caloriesFromMacros(10, 20, 5) = 10*4 + 20*4 + 5*9 = 40+80+45 = 165
        assertEquals(165, scaled.calories)
        assertEquals(10, scaled.protein)
        assertEquals(20, scaled.carbs)
        assertEquals(5, scaled.fat)
    }

    @Test
    fun scaledTo_zeroPortion_returnsZeros() {
        val nutriments = OpenFoodFactsNutriments(
            energyKcal100g = 200.0,
            proteins100g = 10.0,
            fat100g = 5.0,
            carbohydrates100g = 25.0,
        )
        val scaled = nutriments.scaledTo(0)
        assertEquals(0, scaled.calories)
        assertEquals(0, scaled.protein)
        assertEquals(0, scaled.carbs)
        assertEquals(0, scaled.fat)
    }
}
