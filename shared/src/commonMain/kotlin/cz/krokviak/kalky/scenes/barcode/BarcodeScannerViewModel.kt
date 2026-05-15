package cz.krokviak.kalky.scenes.barcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalky.scenes.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalky.core.common.error.UiError
import cz.krokviak.kalky.core.common.error.toUiError
import cz.krokviak.kalky.core.network.OpenFoodFactsClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class BarcodeScanState {
    data object Scanning : BarcodeScanState()
    data object Loading : BarcodeScanState()
    data class ProductFound(val product: OpenFoodFactsProduct, val barcode: String) : BarcodeScanState()
    data object NotFound : BarcodeScanState()
    data class Error(val error: UiError) : BarcodeScanState()
}

class BarcodeScannerViewModel(
    private val openFoodFactsClient: OpenFoodFactsClient
) : ViewModel() {
    private val _state = MutableStateFlow<BarcodeScanState>(BarcodeScanState.Scanning)
    val state: StateFlow<BarcodeScanState> = _state

    private var lastScannedBarcode: String? = null

    fun onBarcodeDetected(barcode: String) {
        if (_state.value !is BarcodeScanState.Scanning) return
        if (barcode == lastScannedBarcode) return
        lastScannedBarcode = barcode

        _state.value = BarcodeScanState.Loading

        viewModelScope.launch {
            _state.value = lookupProduct(barcode)
        }
    }

    private suspend fun lookupProduct(barcode: String): BarcodeScanState = runCatching {
        openFoodFactsClient.getProduct(barcode)
    }.fold(
        onSuccess = { product -> productState(product, barcode) },
        onFailure = { BarcodeScanState.Error(it.toUiError(UiError.ProductSearch)) },
    )

    private fun productState(product: OpenFoodFactsProduct?, barcode: String): BarcodeScanState =
        if (product?.productName != null) BarcodeScanState.ProductFound(product, barcode)
        else BarcodeScanState.NotFound

    fun resetScan() {
        lastScannedBarcode = null
        _state.value = BarcodeScanState.Scanning
    }
}
