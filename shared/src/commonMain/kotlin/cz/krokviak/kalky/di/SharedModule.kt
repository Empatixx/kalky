package cz.krokviak.kalky.di

import cz.krokviak.kalky.common.AppPreferences
import cz.krokviak.kalky.common.FoodPhotoAnalyzer
import cz.krokviak.kalky.common.StreakCalculator
import cz.krokviak.kalky.common.domain.AddFoodItemUseCase
import cz.krokviak.kalky.common.domain.BuildCaloriesBarsUseCase
import cz.krokviak.kalky.common.domain.DeleteFoodItemsUseCase
import cz.krokviak.kalky.common.domain.GetDailyMacrosUseCase
import cz.krokviak.kalky.common.domain.GetLatestNutrientSettingsUseCase
import cz.krokviak.kalky.common.domain.GetStreakUseCase
import cz.krokviak.kalky.common.domain.UpdateNutrientSettingsUseCase
import cz.krokviak.kalky.common.repo.FoodRepository
import cz.krokviak.kalky.common.repo.NutrientSettingRepo
import cz.krokviak.kalky.common.repo.NutrientSettingRepoImpl
import cz.krokviak.kalky.common.repo.PersonalInfoRepo
import cz.krokviak.kalky.notifications.MealReminderChecker
import cz.krokviak.kalky.db.DatabaseSeeder
import cz.krokviak.kalky.db.createDatabase
import cz.krokviak.kalky.network.FoodAnalysisClient
import cz.krokviak.kalky.network.OpenFoodFactsClient
import cz.krokviak.kalky.network.createHttpClient
import io.ktor.client.HttpClient
import kotlinx.datetime.Clock
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val sharedModule = module {
    // Time source (injected so tests can swap in a TestClock)
    single<Clock> { Clock.System }

    // Preferences
    single { AppPreferences() }

    // Network
    single<HttpClient> { createHttpClient(getOrNull(), getOrNull()) }
    single {
        FoodAnalysisClient(
            httpClient = get(),
            baseUrl = getOrNull<String>(named("backendBaseUrl")) ?: FoodAnalysisClient.DEFAULT_BASE_URL
        )
    }
    single { OpenFoodFactsClient(get()) }

    // Database
    single { createDatabase(get()) }
    single { DatabaseSeeder(get()) }

    // Repositories
    single { FoodRepository(get()) }
    single { PersonalInfoRepo(get()) }
    single<NutrientSettingRepo> { NutrientSettingRepoImpl(get()) }

    // Streak
    single { StreakCalculator(get()) }

    // Food photo pipeline
    single { FoodPhotoAnalyzer(get(), get(), get(), get()) }

    // Domain use cases
    factory { GetDailyMacrosUseCase(get()) }
    factory { GetStreakUseCase(get()) }
    factory { BuildCaloriesBarsUseCase(get()) }
    factory { AddFoodItemUseCase(get()) }
    factory { DeleteFoodItemsUseCase(get()) }
    factory { UpdateNutrientSettingsUseCase(get()) }
    factory { GetLatestNutrientSettingsUseCase(get()) }

    // Notifications
    single { MealReminderChecker(get(), get()) }
}
