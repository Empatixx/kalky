package cz.krokviak.kalky.core.di

import cz.krokviak.kalky.core.common.DeepLinkBus
import cz.krokviak.kalky.scenes.auth.AuthViewModelInterface
import cz.krokviak.kalky.scenes.barcode.BarcodeScannerViewModel
import cz.krokviak.kalky.scenes.home.MainViewModel
import org.koin.mp.KoinPlatform

fun resolveAuthViewModel(): AuthViewModelInterface = KoinPlatform.getKoin().get()

fun resolveMainViewModel(): MainViewModel = KoinPlatform.getKoin().get()

fun resolveBarcodeScannerViewModel(): BarcodeScannerViewModel = KoinPlatform.getKoin().get()

fun openFoodDetailDeepLink(id: Long) {
    KoinPlatform.getKoin().get<DeepLinkBus>().openFoodDetail(id)
}
