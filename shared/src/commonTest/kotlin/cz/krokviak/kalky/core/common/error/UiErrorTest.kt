package cz.krokviak.kalky.core.common.error

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UiErrorTest {

    @Test
    fun toUiError_mapsNetworkKeywords_toNetworkError() {
        val errors = listOf(
            Throwable("UnresolvedAddressException: failed"),
            Throwable("java.net.ConnectException"),
            Throwable("Read timeout"),
            Throwable("Network is down"),
        )
        errors.forEach { e ->
            assertEquals(UiError.Network, e.toUiError(), "expected Network for: ${e.message}")
        }
    }

    @Test
    fun toUiError_returnsDefault_forUnrecognizedThrowable() {
        val default = UiError.Generic("custom default")
        val mapped = Throwable("nějaká divná chyba").toUiError(default)
        assertEquals(default, mapped)
    }

    @Test
    fun toUiError_returnsGenericFallback_whenNoDefaultProvided() {
        val mapped = Throwable("unknown").toUiError()
        assertIs<UiError.Generic>(mapped)
    }

    @Test
    fun toMessage_handlesAllSealedSubtypes() {
        assertEquals("Chyba sítě, zkontroluj připojení", UiError.Network.toMessage())
        assertEquals("Nepodařilo se analyzovat fotku", UiError.PhotoAnalysis.toMessage())
        assertEquals("Vyhledávání produktů selhalo", UiError.ProductSearch.toMessage())
        assertEquals("Produkt nebyl nalezen", UiError.BarcodeNotFound.toMessage())
        assertEquals("custom", UiError.Generic("custom").toMessage())
    }
}
