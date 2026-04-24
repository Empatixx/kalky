package cz.krokviak.kalky.di

import cz.krokviak.kalky.common.AppPreferences
import cz.krokviak.kalky.common.FoodPhotoAnalyzer
import cz.krokviak.kalky.common.StreakCalculator
import cz.krokviak.kalky.common.repo.FoodRepository
import cz.krokviak.kalky.common.repo.NutrientSettingRepo
import cz.krokviak.kalky.common.repo.PersonalInfoRepo
import cz.krokviak.kalky.notifications.MealReminderChecker
import cz.krokviak.kalky.db.DatabaseSeeder
import cz.krokviak.kalky.db.createDatabase
import cz.krokviak.kalky.network.FoodAnalysisClient
import cz.krokviak.kalky.network.OpenFoodFactsClient
import cz.krokviak.kalky.network.createHttpClient
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val sharedModule = module {
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
    single { NutrientSettingRepo(get()) }

    // Streak
    single { StreakCalculator(get()) }

    // Food photo pipeline
    single { FoodPhotoAnalyzer(get(), get(), get()) }

    // Notifications
    single { MealReminderChecker(get(), get()) }
}
