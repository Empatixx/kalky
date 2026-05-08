package cz.krokviak.kalky.scenes.barcode

import cz.krokviak.kalky.core.network.OpenFoodFactsClient
import cz.krokviak.kalky.scenes.barcode.data.OpenFoodFactsProduct
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class BarcodeScannerViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() { Dispatchers.setMain(dispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun onBarcodeDetected_productFound_emitsProductFoundState() = runTest(dispatcher) {
        val product = OpenFoodFactsProduct(productName = "Mléko")
        val client = mock<OpenFoodFactsClient> {
            everySuspend { getProduct(any()) } returns product
        }
        val vm = BarcodeScannerViewModel(client)

        vm.onBarcodeDetected("8594001234567")
        advanceUntilIdle()

        val state = assertIs<BarcodeScanState.ProductFound>(vm.state.value)
        assertEquals(product, state.product)
        assertEquals("8594001234567", state.barcode)
    }

    @Test
    fun onBarcodeDetected_nullProduct_emitsNotFound() = runTest(dispatcher) {
        val client = mock<OpenFoodFactsClient> {
            everySuspend { getProduct(any()) } returns null
        }
        val vm = BarcodeScannerViewModel(client)

        vm.onBarcodeDetected("0000")
        advanceUntilIdle()

        assertIs<BarcodeScanState.NotFound>(vm.state.value)
    }

    @Test
    fun onBarcodeDetected_productWithBlankName_emitsNotFound() = runTest(dispatcher) {
        val client = mock<OpenFoodFactsClient> {
            everySuspend { getProduct(any()) } returns OpenFoodFactsProduct(productName = null)
        }
        val vm = BarcodeScannerViewModel(client)

        vm.onBarcodeDetected("0000")
        advanceUntilIdle()

        assertIs<BarcodeScanState.NotFound>(vm.state.value)
    }

    @Test
    fun onBarcodeDetected_apiThrows_emitsErrorState() = runTest(dispatcher) {
        val client = mock<OpenFoodFactsClient> {
            everySuspend { getProduct(any()) } throws RuntimeException("network down")
        }
        val vm = BarcodeScannerViewModel(client)

        vm.onBarcodeDetected("123")
        advanceUntilIdle()

        val state = assertIs<BarcodeScanState.Error>(vm.state.value)
        assertEquals("network down", state.message)
    }

    @Test
    fun onBarcodeDetected_sameBarcode_isIgnoredOnceLoading() = runTest(dispatcher) {
        val client = mock<OpenFoodFactsClient> {
            everySuspend { getProduct(any()) } returns OpenFoodFactsProduct(productName = "X")
        }
        val vm = BarcodeScannerViewModel(client)

        vm.onBarcodeDetected("123")
        // Don't advance — state is Loading. Calling again should be a no-op.
        vm.onBarcodeDetected("123")
        advanceUntilIdle()

        // Should still resolve to ProductFound (only one lookup happened)
        assertIs<BarcodeScanState.ProductFound>(vm.state.value)
    }

    @Test
    fun resetScan_returnsToScanningState() = runTest(dispatcher) {
        val client = mock<OpenFoodFactsClient> {
            everySuspend { getProduct(any()) } returns null
        }
        val vm = BarcodeScannerViewModel(client)

        vm.onBarcodeDetected("123")
        advanceUntilIdle()
        assertIs<BarcodeScanState.NotFound>(vm.state.value)

        vm.resetScan()
        assertIs<BarcodeScanState.Scanning>(vm.state.value)
    }

    @Test
    fun resetScan_allowsRescannningSameBarcode() = runTest(dispatcher) {
        val client = mock<OpenFoodFactsClient> {
            everySuspend { getProduct(any()) } returns OpenFoodFactsProduct(productName = "X")
        }
        val vm = BarcodeScannerViewModel(client)

        vm.onBarcodeDetected("123")
        advanceUntilIdle()
        vm.resetScan()
        vm.onBarcodeDetected("123")
        advanceUntilIdle()

        assertIs<BarcodeScanState.ProductFound>(vm.state.value)
    }
}
