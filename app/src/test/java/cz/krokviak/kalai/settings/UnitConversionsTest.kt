package cz.krokviak.kalai.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class UnitConversionsTest {

    @Test
    fun `kg lb conversion roundtrip stays precise`() {
        val originalKg = 82.4f
        val lb = convertKgToLb(originalKg)
        val convertedKg = convertLbToKg(lb)

        assertEquals(originalKg, convertedKg, 0.0001f)
    }

    @Test
    fun `cm inch conversion roundtrip stays precise`() {
        val originalCm = 178.0f
        val inches = convertCmToIn(originalCm)
        val convertedCm = convertInToCm(inches)

        assertEquals(originalCm, convertedCm, 0.0001f)
    }

    @Test
    fun `display formatting uses expected units`() {
        assertEquals("70.0", formatWeightForDisplay(70f, UnitSystem.METRIC))
        assertEquals("154.3", formatWeightForDisplay(70f, UnitSystem.IMPERIAL))
        assertEquals("180", formatHeightForDisplay(180f, UnitSystem.METRIC))
        assertEquals("70.9", formatHeightForDisplay(180f, UnitSystem.IMPERIAL))
    }
}
