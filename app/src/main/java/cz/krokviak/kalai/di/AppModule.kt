package cz.krokviak.kalai.di

import cz.krokviak.kalai.analytics.AnalyticsViewModel
import cz.krokviak.kalai.detail.FoodDetailViewModel
import cz.krokviak.kalai.home.MainViewModel
import cz.krokviak.kalai.common.repo.FoodRepository
import cz.krokviak.kalai.common.repo.NutrientSettingRepo
import cz.krokviak.kalai.common.repo.PersonalInfoRepo
import cz.krokviak.kalai.nutrientedit.NutrientEditViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { NutrientSettingRepo() }
    single { FoodRepository(get()) }
    single { PersonalInfoRepo(get()) }

    viewModel { MainViewModel(get(), get()) }
    viewModel { FoodDetailViewModel() }
    viewModel { NutrientEditViewModel(get()) }
    viewModel { AnalyticsViewModel(get(), get()) }

}