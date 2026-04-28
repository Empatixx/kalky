package cz.krokviak.kalky.common.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class CalorieUtilsTest {

    @Test
    fun `caloriesFromMacros uses 4-4-9 ratio for Int`() {
        assertEquals(0, caloriesFromMacros(0, 0, 0))
        assertEquals(40, caloriesFromMacros(10, 0, 0))
        assertEquals(40, caloriesFromMacros(0, 10, 0))
        assertEquals(90, caloriesFromMacros(0, 0, 10))
        assertEquals(170, caloriesFromMacros(10, 10, 10))
    }

    @Test
    fun `caloriesFromMacros uses 4-4-9 ratio for Double`() {
        assertEquals(0.0, caloriesFromMacros(0.0, 0.0, 0.0))
        assertEquals(170.0, caloriesFromMacros(10.0, 10.0, 10.0))
        assertEquals(22.5, caloriesFromMacros(0.0, 0.0, 2.5))
    }
}
