package cz.krokviak.kalky.di

import cz.krokviak.kalky.analytics.AnalyticsViewModel
import cz.krokviak.kalky.auth.AppCheckTokenProvider
import cz.krokviak.kalky.auth.AuthStateProvider
import cz.krokviak.kalky.auth.AuthTokenProvider
import cz.krokviak.kalky.auth.AuthViewModel
import cz.krokviak.kalky.auth.FirebaseAppCheckTokenProvider
import cz.krokviak.kalky.auth.FirebaseAuthTokenProvider
import cz.krokviak.kalky.barcode.BarcodeScannerViewModel
import cz.krokviak.kalky.camera.CameraViewModel
import cz.krokviak.kalky.common.AndroidImageStorage
import cz.krokviak.kalky.common.ImageStorage
import cz.krokviak.kalky.customfood.CustomFoodSearchViewModel
import cz.krokviak.kalky.customfood.ManualFoodEntryViewModel
import cz.krokviak.kalky.db.DriverFactory
import cz.krokviak.kalky.detail.FoodDetailViewModel
import cz.krokviak.kalky.home.MainViewModel
import cz.krokviak.kalky.nutrientedit.NutrientEditViewModel
import cz.krokviak.kalky.onboarding.OnboardingViewModel
import cz.krokviak.kalky.settings.SettingsViewModel
import cz.krokviak.kalky.BuildConfig
import cz.krokviak.kalky.config.RemoteConfigManager
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
    single<AppCheckTokenProvider> { FirebaseAppCheckTokenProvider() }

    // ViewModels
    viewModel { MainViewModel(get(), get(), get(), get(), get(), get(), get(), seedMockData = BuildConfig.DEBUG) }
    viewModel { FoodDetailViewModel(get(), get(), get(), get()) }
    viewModel { NutrientEditViewModel(get()) }
    viewModel { AnalyticsViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { BarcodeScannerViewModel(get()) }
    viewModel { CameraViewModel(get()) }
    viewModel { CustomFoodSearchViewModel(get(), get(), get()) }
    viewModel { ManualFoodEntryViewModel(get(), get(), get()) }
    viewModel { AuthViewModel(get(), get()) }
}
