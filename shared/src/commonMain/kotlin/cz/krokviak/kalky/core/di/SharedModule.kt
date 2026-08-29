package cz.krokviak.kalky.core.di

import cz.krokviak.kalky.core.common.AppPreferences
import cz.krokviak.kalky.core.common.DeepLinkBus
import cz.krokviak.kalky.core.common.FoodPhotoAnalyzer
import cz.krokviak.kalky.core.common.StreakCalculator
import cz.krokviak.kalky.core.common.domain.AddFoodItemUseCase
import cz.krokviak.kalky.core.common.domain.BuildCaloriesBarsUseCase
import cz.krokviak.kalky.core.common.domain.CompleteOnboardingUseCase
import cz.krokviak.kalky.core.common.domain.DeleteFoodItemUseCase
import cz.krokviak.kalky.core.common.domain.DeleteFoodItemsUseCase
import cz.krokviak.kalky.core.common.domain.GetDailyMacrosUseCase
import cz.krokviak.kalky.core.common.domain.GetFoodItemUseCase
import cz.krokviak.kalky.core.common.domain.GetFoodLibraryUseCase
import cz.krokviak.kalky.core.common.domain.GetLatestNutrientSettingsUseCase
import cz.krokviak.kalky.core.common.domain.GetStreakUseCase
import cz.krokviak.kalky.core.common.domain.GetWeightsInRangeUseCase
import cz.krokviak.kalky.core.common.domain.ObserveDailyMacrosUseCase
import cz.krokviak.kalky.core.common.domain.SearchFoodsUseCase
import cz.krokviak.kalky.core.common.domain.SearchHistoryFoodsUseCase
import cz.krokviak.kalky.core.common.domain.UpdateFoodItemUseCase
import cz.krokviak.kalky.core.common.domain.UpdateNutrientSettingsUseCase
import cz.krokviak.kalky.core.common.repo.FoodRepository
import cz.krokviak.kalky.core.common.repo.NutrientSettingRepo
import cz.krokviak.kalky.core.common.repo.NutrientSettingRepoImpl
import cz.krokviak.kalky.core.common.repo.PersonalInfoRepo
import cz.krokviak.kalky.core.notifications.MealReminderChecker
import cz.krokviak.kalky.core.db.DatabaseSeeder
import cz.krokviak.kalky.core.db.createDatabase
import cz.krokviak.kalky.core.network.FoodAnalysisClient
import cz.krokviak.kalky.core.network.OpenFoodFactsClient
import cz.krokviak.kalky.core.network.createHttpClient
import io.ktor.client.HttpClient
import kotlinx.datetime.Clock
import org.koin.core.qualifier.named
import org.koin.dsl.module

val sharedModule = module {

    single<Clock> { Clock.System }

    single { AppPreferences() }

    single { DeepLinkBus() }

    single<HttpClient> { createHttpClient(getOrNull(), getOrNull()) }
    single {
        FoodAnalysisClient(
            httpClient = get(),
            baseUrl = getOrNull<String>(named("backendBaseUrl")) ?: FoodAnalysisClient.DEFAULT_BASE_URL
        )
    }
    single { OpenFoodFactsClient(get()) }

    single { createDatabase(get()) }
    single { DatabaseSeeder(get()) }

    single { FoodRepository(get()) }
    single { PersonalInfoRepo(get()) }
    single<NutrientSettingRepo> { NutrientSettingRepoImpl(get()) }

    single { StreakCalculator(get()) }

    single { FoodPhotoAnalyzer(get(), get(), get(), get()) }

    factory { GetDailyMacrosUseCase(get()) }
    factory { GetStreakUseCase(get()) }
    factory { BuildCaloriesBarsUseCase(get()) }
    factory { AddFoodItemUseCase(get()) }
    factory { DeleteFoodItemsUseCase(get()) }
    factory { UpdateNutrientSettingsUseCase(get()) }
    factory { GetLatestNutrientSettingsUseCase(get()) }
    factory { CompleteOnboardingUseCase(get(), get()) }
    factory { GetFoodItemUseCase(get()) }
    factory { UpdateFoodItemUseCase(get()) }
    factory { DeleteFoodItemUseCase(get()) }
    factory { SearchFoodsUseCase(get()) }
    factory { SearchHistoryFoodsUseCase(get()) }
    factory { GetFoodLibraryUseCase(get()) }
    factory { GetWeightsInRangeUseCase(get()) }
    factory { ObserveDailyMacrosUseCase(get()) }

    single { MealReminderChecker(get(), get()) }
}
