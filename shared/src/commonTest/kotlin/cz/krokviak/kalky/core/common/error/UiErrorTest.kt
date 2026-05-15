package cz.krokviak.kalky.core.common.error

import cz.krokviak.kalky.core.i18n.CzechStrings
import cz.krokviak.kalky.core.i18n.EnglishStrings
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
        val mapped = Throwable("nějaká divná chyba").toUiError(UiError.PhotoAnalysis)
        assertEquals(UiError.PhotoAnalysis, mapped)
    }

    @Test
    fun toUiError_returnsGenericFallback_whenNoDefaultProvided() {
        val mapped = Throwable("unknown").toUiError()
        assertIs<UiError.Generic>(mapped)
    }

    @Test
    fun toMessage_resolvesAllSubtypes_viaCzechStrings() {
        val s = CzechStrings.common
        assertEquals("Chyba sítě, zkontroluj připojení", UiError.Network.toMessage(s))
        assertEquals("Nepodařilo se analyzovat fotku", UiError.PhotoAnalysis.toMessage(s))
        assertEquals("Vyhledávání produktů selhalo", UiError.ProductSearch.toMessage(s))
        assertEquals("Něco se nepodařilo", UiError.BarcodeNotFound.toMessage(s))
        assertEquals("Něco se nepodařilo", UiError.Generic.toMessage(s))
    }

    @Test
    fun toMessage_resolvesAllSubtypes_viaEnglishStrings() {
        val s = EnglishStrings.common
        assertEquals("Network error, check your connection", UiError.Network.toMessage(s))
        assertEquals("Photo analysis failed", UiError.PhotoAnalysis.toMessage(s))
        assertEquals("Product search failed", UiError.ProductSearch.toMessage(s))
        assertEquals("Something went wrong", UiError.Generic.toMessage(s))
    }
}
