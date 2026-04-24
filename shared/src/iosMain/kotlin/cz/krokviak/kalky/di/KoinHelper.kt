package cz.krokviak.kalky.di

import cz.krokviak.kalky.analytics.AnalyticsViewModel
import cz.krokviak.kalky.auth.AppCheckTokenProvider
import cz.krokviak.kalky.auth.AuthStateProvider
import cz.krokviak.kalky.auth.AuthTokenProvider
import cz.krokviak.kalky.auth.AuthViewModelInterface
import cz.krokviak.kalky.auth.StubAppCheckTokenProvider
import cz.krokviak.kalky.auth.StubAuthStateProvider
import cz.krokviak.kalky.auth.StubAuthTokenProvider
import cz.krokviak.kalky.auth.StubAuthViewModel
import cz.krokviak.kalky.common.IosImageStorage
import cz.krokviak.kalky.common.ImageStorage
import cz.krokviak.kalky.customfood.CustomFoodSearchViewModel
import cz.krokviak.kalky.customfood.ManualFoodEntryViewModel
import cz.krokviak.kalky.db.DriverFactory
import cz.krokviak.kalky.detail.FoodDetailViewModel
import cz.krokviak.kalky.home.MainViewModel
import cz.krokviak.kalky.nutrientedit.NutrientEditViewModel
import cz.krokviak.kalky.onboarding.OnboardingViewModel
import cz.krokviak.kalky.settings.SettingsViewModel
import org.koin.core.context.startKoin
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

                // ViewModels
                single { MainViewModel(get(), get(), get(), get(), get(), get()) }
                single { FoodDetailViewModel(get(), get(), get()) }
                single { NutrientEditViewModel(get()) }
                single { AnalyticsViewModel(get(), get()) }
                single { SettingsViewModel(get()) }
                single { OnboardingViewModel(get()) }
                single { CustomFoodSearchViewModel(get(), get()) }
                single { ManualFoodEntryViewModel(get(), get()) }
                single { cz.krokviak.kalky.barcode.BarcodeScannerViewModel(get()) }
                single<AuthViewModelInterface> { StubAuthViewModel() }
            }
        )
    }
}
