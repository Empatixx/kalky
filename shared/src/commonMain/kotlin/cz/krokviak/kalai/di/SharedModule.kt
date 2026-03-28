package cz.krokviak.kalai.di

import cz.krokviak.kalai.common.StreakCalculator
import cz.krokviak.kalai.common.repo.FoodRepository
import cz.krokviak.kalai.common.repo.NutrientSettingRepo
import cz.krokviak.kalai.common.repo.PersonalInfoRepo
import cz.krokviak.kalai.notifications.MealReminderChecker
import cz.krokviak.kalai.db.createDatabase
import cz.krokviak.kalai.network.FoodAnalysisClient
import cz.krokviak.kalai.network.OpenFoodFactsClient
import cz.krokviak.kalai.network.createHttpClient
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val sharedModule = module {
    // Network
    single<HttpClient> { createHttpClient(getOrNull()) }
    single {
        FoodAnalysisClient(
            httpClient = get(),
            baseUrl = getOrNull<String>(named("backendBaseUrl")) ?: FoodAnalysisClient.DEFAULT_BASE_URL
        )
    }
    single { OpenFoodFactsClient(get()) }

    // Database
    single { createDatabase(get()) }

    // Repositories
    single { FoodRepository(get()) }
    single { PersonalInfoRepo(get()) }
    single { NutrientSettingRepo(get()) }

    // Streak
    single { StreakCalculator(get()) }

    // Notifications
    single { MealReminderChecker(get(), get()) }
}
