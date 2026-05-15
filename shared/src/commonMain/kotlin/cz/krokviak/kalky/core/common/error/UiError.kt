package cz.krokviak.kalky.core.common.error

import androidx.compose.runtime.Immutable
import cz.krokviak.kalky.core.i18n.CommonStrings

@Immutable
sealed interface UiError {
    data object Network : UiError
    data object PhotoAnalysis : UiError
    data object ProductSearch : UiError
    data object BarcodeNotFound : UiError
    data object Generic : UiError
}

fun Throwable.toUiError(default: UiError = UiError.Generic): UiError {
    val msg = message ?: ""
    return when {
        msg.contains("UnresolvedAddress", ignoreCase = true) ||
            msg.contains("ConnectException", ignoreCase = true) ||
            msg.contains("timeout", ignoreCase = true) ||
            msg.contains("network", ignoreCase = true) -> UiError.Network
        else -> default
    }
}

fun UiError.toMessage(strings: CommonStrings): String = when (this) {
    UiError.Network -> strings.errorNetwork
    UiError.PhotoAnalysis -> strings.errorPhotoAnalysis
    UiError.ProductSearch -> strings.errorProductSearch
    UiError.BarcodeNotFound -> strings.errorGeneric
    UiError.Generic -> strings.errorGeneric
}
