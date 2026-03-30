package cz.krokviak.kalky.barcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.krokviak.kalky.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalky.network.OpenFoodFactsClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class BarcodeScanState {
    data object Scanning : BarcodeScanState()
    data object Loading : BarcodeScanState()
    data class ProductFound(val product: OpenFoodFactsProduct, val barcode: String) : BarcodeScanState()
    data object NotFound : BarcodeScanState()
    data class Error(val message: String) : BarcodeScanState()
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
            try {
                val product = openFoodFactsClient.getProduct(barcode)
                if (product != null && product.productName != null) {
                    _state.value = BarcodeScanState.ProductFound(product, barcode)
                } else {
                    _state.value = BarcodeScanState.NotFound
                }
            } catch (e: Exception) {
                _state.value = BarcodeScanState.Error(e.message ?: "Neznámá chyba")
            }
        }
    }

    fun resetScan() {
        lastScannedBarcode = null
        _state.value = BarcodeScanState.Scanning
    }
}
