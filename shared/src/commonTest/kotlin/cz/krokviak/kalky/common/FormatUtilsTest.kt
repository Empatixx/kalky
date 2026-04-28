package cz.krokviak.kalky.common

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatUtilsTest {

    @Test
    fun formatFloat1_exactTenth() {
        assertEquals("1.5", formatFloat1(1.5f))
        assertEquals("0.0", formatFloat1(0.0f))
        assertEquals("10.0", formatFloat1(10.0f))
    }

    @Test
    fun formatFloat1_roundsUpToNextTenth() {
        // 1.96f * 10 ≈ 19.6 → 20 → "2.0"
        assertEquals("2.0", formatFloat1(1.96f))
    }

    @Test
    fun formatFloat1_truncatesExtraDigitsPastFirstFraction() {
        // 3.14f * 10 = 31.4 → 31 → "3.1"
        assertEquals("3.1", formatFloat1(3.14f))
    }

    @Test
    fun formatFloat1_handlesNegative() {
        assertEquals("-1.5", formatFloat1(-1.5f))
    }
}
