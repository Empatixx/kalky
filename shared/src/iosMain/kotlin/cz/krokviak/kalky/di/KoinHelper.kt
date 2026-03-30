package cz.krokviak.kalky.di

import cz.krokviak.kalky.db.DriverFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoinIos() {
    startKoin {
        modules(
            sharedModule,
            module {
                single { DriverFactory() }
            }
        )
    }
}
