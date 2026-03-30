package cz.krokviak.kalky.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.mp.KoinPlatformTools

/**
 * Simple koinInject() for Compose Multiplatform, compatible with Koin 3.2.x.
 * Retrieves an instance from the global Koin container.
 */
@Composable
inline fun <reified T : Any> koinInject(): T {
    return remember { KoinPlatformTools.defaultContext().get().get<T>() }
}
