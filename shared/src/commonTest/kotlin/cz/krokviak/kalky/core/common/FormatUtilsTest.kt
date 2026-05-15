package cz.krokviak.kalky.core.common

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

        assertEquals("2.0", formatFloat1(1.96f))
    }

    @Test
    fun formatFloat1_truncatesExtraDigitsPastFirstFraction() {

        assertEquals("3.1", formatFloat1(3.14f))
    }

    @Test
    fun formatFloat1_handlesNegative() {
        assertEquals("-1.5", formatFloat1(-1.5f))
    }
}
