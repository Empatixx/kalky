package cz.krokviak.kalai.di

import cz.krokviak.kalai.db.DriverFactory
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
