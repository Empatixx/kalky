package cz.krokviak.kalky.core.di

import cz.krokviak.kalky.scenes.analytics.AnalyticsViewModel
import cz.krokviak.kalky.scenes.auth.AppCheckTokenProvider
import cz.krokviak.kalky.scenes.auth.AuthStateProvider
import cz.krokviak.kalky.scenes.auth.AuthTokenProvider
import cz.krokviak.kalky.scenes.auth.AuthViewModel
import cz.krokviak.kalky.scenes.auth.AuthViewModelInterface
import cz.krokviak.kalky.scenes.auth.FirebaseAppCheckTokenProvider
import cz.krokviak.kalky.scenes.auth.FirebaseAuthTokenProvider
import cz.krokviak.kalky.scenes.barcode.BarcodeScannerViewModel
import cz.krokviak.kalky.core.camera.CameraViewModel
import cz.krokviak.kalky.core.common.AndroidImageStorage
import cz.krokviak.kalky.core.common.ImageStorage
import cz.krokviak.kalky.scenes.customfood.CustomFoodSearchViewModel
import cz.krokviak.kalky.scenes.customfood.ManualFoodEntryViewModel
import cz.krokviak.kalky.core.db.DriverFactory
import cz.krokviak.kalky.scenes.detail.FoodDetailViewModel
import cz.krokviak.kalky.scenes.home.MainViewModel
import cz.krokviak.kalky.scenes.nutrientedit.NutrientEditViewModel
import cz.krokviak.kalky.scenes.onboarding.OnboardingViewModel
import cz.krokviak.kalky.scenes.settings.SettingsViewModel
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

    // App-wide ViewModels (single instance per app — scenes inject via koinInject)
    single { MainViewModel(get(), get(), get(), get(), get(), get(), get(), get(), seedMockData = BuildConfig.DEBUG) }
    single { FoodDetailViewModel(get(), get(), get(), get(), get(), get()) }
    single { NutrientEditViewModel(get(), get()) }
    single { AnalyticsViewModel(get(), get()) }
    single { SettingsViewModel(get()) }
    single { OnboardingViewModel(get()) }
    single { CustomFoodSearchViewModel(get(), get(), get(), get(), get()) }
    single { ManualFoodEntryViewModel(get(), get(), get(), get()) }
    single { AuthViewModel(get(), get()) }
    single<AuthViewModelInterface> { get<AuthViewModel>() }

    // Activity-scoped ViewModels for separate Activities (CameraActivity / BarcodeScannerActivity)
    viewModel { BarcodeScannerViewModel(get()) }
    viewModel { CameraViewModel(get()) }
}
