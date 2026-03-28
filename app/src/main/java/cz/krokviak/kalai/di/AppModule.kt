package cz.krokviak.kalai.di

import cz.krokviak.kalai.analytics.AnalyticsViewModel
import cz.krokviak.kalai.auth.AuthStateProvider
import cz.krokviak.kalai.auth.AuthTokenProvider
import cz.krokviak.kalai.auth.AuthViewModel
import cz.krokviak.kalai.auth.FirebaseAuthTokenProvider
import cz.krokviak.kalai.barcode.BarcodeScannerViewModel
import cz.krokviak.kalai.camera.CameraViewModel
import cz.krokviak.kalai.common.AndroidImageStorage
import cz.krokviak.kalai.common.ImageStorage
import cz.krokviak.kalai.customfood.CustomFoodViewModel
import cz.krokviak.kalai.db.DriverFactory
import cz.krokviak.kalai.detail.FoodDetailViewModel
import cz.krokviak.kalai.home.MainViewModel
import cz.krokviak.kalai.nutrientedit.NutrientEditViewModel
import cz.krokviak.kalai.onboarding.OnboardingViewModel
import cz.krokviak.kalai.settings.SettingsViewModel
import cz.krokviak.kalai.config.RemoteConfigManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    // Platform-specific
    single { DriverFactory(get()) }
    single<ImageStorage> { AndroidImageStorage(get()) }

    // Remote Config - provides backend URL override for shared module
    single(named("backendBaseUrl")) { RemoteConfigManager.getBackendBaseUrl() }

    // Auth
    single { FirebaseAuthTokenProvider() }
    single<AuthTokenProvider> { get<FirebaseAuthTokenProvider>() }
    single<AuthStateProvider> { get<FirebaseAuthTokenProvider>() }

    // ViewModels
    viewModel { MainViewModel(get(), get(), get(), get(), get()) }
    viewModel { FoodDetailViewModel(get(), get(), get()) }
    viewModel { NutrientEditViewModel(get()) }
    viewModel { AnalyticsViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { BarcodeScannerViewModel(get()) }
    viewModel { CameraViewModel(get()) }
    viewModel { CustomFoodViewModel(get(), get()) }
    viewModel { AuthViewModel(get(), get()) }
}
