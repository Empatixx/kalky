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
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { DriverFactory(get()) }
    single<ImageStorage> { AndroidImageStorage(get()) }

    single(named("backendBaseUrl")) { RemoteConfigManager.getBackendBaseUrl() }

    single { FirebaseAuthTokenProvider() }
    single<AuthTokenProvider> { get<FirebaseAuthTokenProvider>() }
    single<AuthStateProvider> { get<FirebaseAuthTokenProvider>() }
    single<AppCheckTokenProvider> { FirebaseAppCheckTokenProvider() }

    viewModel { MainViewModel(get(), get(), get(), get(), get(), get(), get(), get(), seedMockData = BuildConfig.DEBUG) }
    viewModel { FoodDetailViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { NutrientEditViewModel(get(), get()) }
    viewModel { AnalyticsViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { CustomFoodSearchViewModel(get(), get(), get(), get(), get()) }
    single { ManualFoodEntryViewModel(get(), get(), get(), get()) }
    viewModel { AuthViewModel(get(), get()) } bind AuthViewModelInterface::class
    viewModel { BarcodeScannerViewModel(get()) }
    viewModel { CameraViewModel(get()) }
}
