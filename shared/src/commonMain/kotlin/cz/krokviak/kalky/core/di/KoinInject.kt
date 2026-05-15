package cz.krokviak.kalky.core.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.mp.KoinPlatformTools

@Composable
inline fun <reified T : Any> koinInject(): T {
    return remember { KoinPlatformTools.defaultContext().get().get<T>() }
}
