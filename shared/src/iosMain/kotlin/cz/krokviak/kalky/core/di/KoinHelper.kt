package cz.krokviak.kalky.core.di

import cz.krokviak.kalky.scenes.analytics.AnalyticsViewModel
import cz.krokviak.kalky.scenes.auth.AppCheckTokenProvider
import cz.krokviak.kalky.scenes.auth.AuthStateProvider
import cz.krokviak.kalky.scenes.auth.AuthTokenProvider
import cz.krokviak.kalky.scenes.auth.AuthViewModelInterface
import cz.krokviak.kalky.scenes.auth.StubAppCheckTokenProvider
import cz.krokviak.kalky.scenes.auth.StubAuthStateProvider
import cz.krokviak.kalky.scenes.auth.StubAuthTokenProvider
import cz.krokviak.kalky.scenes.auth.StubAuthViewModel
import cz.krokviak.kalky.core.common.IosImageStorage
import cz.krokviak.kalky.core.common.ImageStorage
import cz.krokviak.kalky.scenes.customfood.CustomFoodSearchViewModel
import cz.krokviak.kalky.scenes.customfood.ManualFoodEntryViewModel
import cz.krokviak.kalky.core.db.DriverFactory
import cz.krokviak.kalky.scenes.detail.FoodDetailViewModel
import cz.krokviak.kalky.scenes.home.MainViewModel
import cz.krokviak.kalky.scenes.nutrientedit.NutrientEditViewModel
import cz.krokviak.kalky.scenes.onboarding.OnboardingViewModel
import cz.krokviak.kalky.scenes.settings.SettingsViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun initKoinIos(
    authTokenProvider: AuthTokenProvider? = null,
    authStateProvider: AuthStateProvider? = null,
    appCheckTokenProvider: AppCheckTokenProvider? = null,
    backendBaseUrl: String? = null
) {
    startKoin {
        modules(
            sharedModule,
            module {
                // Platform-specific
                single { DriverFactory() }
                single<ImageStorage> { IosImageStorage() }

                // Auth providers (stubs by default, replaced by Swift implementations later)
                single<AuthTokenProvider> { authTokenProvider ?: StubAuthTokenProvider() }
                single<AuthStateProvider> { authStateProvider ?: StubAuthStateProvider() }
                single<AppCheckTokenProvider> { appCheckTokenProvider ?: StubAppCheckTokenProvider() }

                // Backend URL override
                if (backendBaseUrl != null) {
                    single(org.koin.core.qualifier.named("backendBaseUrl")) { backendBaseUrl }
                }

                // ViewModels — Koin 4 KMP DSL with proper VM lifecycle
                viewModel { MainViewModel(get(), get(), get(), get(), get(), get()) }
                viewModel { FoodDetailViewModel(get(), get(), get()) }
                viewModel { NutrientEditViewModel(get()) }
                viewModel { AnalyticsViewModel(get(), get()) }
                viewModel { SettingsViewModel(get()) }
                viewModel { OnboardingViewModel(get()) }
                viewModel { CustomFoodSearchViewModel(get(), get()) }
                viewModel { ManualFoodEntryViewModel(get(), get()) }
                viewModel { cz.krokviak.kalky.scenes.barcode.BarcodeScannerViewModel(get()) }
                viewModel<AuthViewModelInterface> { StubAuthViewModel() }
            }
        )
    }
}
