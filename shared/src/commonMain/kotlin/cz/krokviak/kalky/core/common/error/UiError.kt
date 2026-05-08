package cz.krokviak.kalky.core.common.error

import androidx.compose.runtime.Immutable

@Immutable
sealed interface UiError {
    data object Network : UiError
    data object PhotoAnalysis : UiError
    data object ProductSearch : UiError
    data object BarcodeNotFound : UiError
    data class Generic(val message: String) : UiError
}

fun Throwable.toUiError(default: UiError = UiError.Generic("Něco se nepodařilo")): UiError {
    val msg = message ?: ""
    return when {
        msg.contains("UnresolvedAddress", ignoreCase = true) ||
            msg.contains("ConnectException", ignoreCase = true) ||
            msg.contains("timeout", ignoreCase = true) ||
            msg.contains("network", ignoreCase = true) -> UiError.Network
        else -> default
    }
}

fun UiError.toMessage(): String = when (this) {
    UiError.Network -> "Chyba sítě, zkontroluj připojení"
    UiError.PhotoAnalysis -> "Nepodařilo se analyzovat fotku"
    UiError.ProductSearch -> "Vyhledávání produktů selhalo"
    UiError.BarcodeNotFound -> "Produkt nebyl nalezen"
    is UiError.Generic -> message
}
