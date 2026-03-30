package cz.krokviak.kalky.di

import org.koin.core.Koin
import org.koin.mp.KoinPlatformTools

fun getKoin(): Koin = KoinPlatformTools.defaultContext().get()
