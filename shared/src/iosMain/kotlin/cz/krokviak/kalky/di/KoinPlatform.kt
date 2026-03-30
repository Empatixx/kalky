package cz.krokviak.kalky.di

import org.koin.core.Koin
import org.koin.core.context.GlobalContext

fun getKoin(): Koin = GlobalContext.get()
