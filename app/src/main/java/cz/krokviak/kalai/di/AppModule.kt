package cz.krokviak.kalai.di

import cz.krokviak.kalai.analytics.AnalyticsViewModel
import cz.krokviak.kalai.barcode.BarcodeScannerViewModel
import cz.krokviak.kalai.common.AndroidImageStorage
import cz.krokviak.kalai.common.ImageStorage
import cz.krokviak.kalai.db.DriverFactory
import cz.krokviak.kalai.detail.FoodDetailViewModel
import cz.krokviak.kalai.home.MainViewModel
import cz.krokviak.kalai.nutrientedit.NutrientEditViewModel
import cz.krokviak.kalai.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Platform-specific
    single { DriverFactory(get()) }
    single<ImageStorage> { AndroidImageStorage(get()) }

    // ViewModels
    viewModel { MainViewModel(get(), get(), get(), get()) }
    viewModel { FoodDetailViewModel(get(), get(), get()) }
    viewModel { NutrientEditViewModel(get()) }
    viewModel { AnalyticsViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { BarcodeScannerViewModel(get()) }
}
