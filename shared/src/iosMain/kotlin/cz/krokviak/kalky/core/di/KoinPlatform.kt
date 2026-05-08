package cz.krokviak.kalky.core.di

import org.koin.core.Koin
import org.koin.mp.KoinPlatformTools

fun getKoin(): Koin = KoinPlatformTools.defaultContext().get()
