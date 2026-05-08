package cz.krokviak.kalky.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.core.i18n.rememberStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Immutable
data class AppColors(
    val background: Color,
    val surface: Color,
    val surfaceSecondary: Color,
    val onBackground: Color,
    val onBackgroundSecondary: Color,
    val border: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryVariant: Color,
    val onPrimaryVariant: Color,
    val chartLine: Color,
    val chartAreaFill: Color,
)

val LightColors = AppColors(
    background = Color(0xFFF4F3F8),
    surface = Color.White,
    surfaceSecondary = Color(0xFFE9E8EF),
    onBackground = Color.Black,
    onBackgroundSecondary = Color(0xFF7E7D87),
    border = Color(0xFFD8D6E0),
    primary = Color.Black,
    onPrimary = Color.White,
    primaryVariant = Color.DarkGray,
    onPrimaryVariant = Color.White,
    chartLine = Color.Black,
    chartAreaFill = Color.Black.copy(alpha = 0.1f),
)

val DarkColors = AppColors(
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceSecondary = Color(0xFF2C2C2C),
    onBackground = Color.White,
    onBackgroundSecondary = Color(0xFF9E9E9E),
    border = Color(0xFF3A3A3A),
    primary = Color.White,
    onPrimary = Color.Black,
    primaryVariant = Color(0xFFBBBBBB),
    onPrimaryVariant = Color.Black,
    chartLine = Color.White,
    chartAreaFill = Color.White.copy(alpha = 0.1f),
)

val LocalAppColors = staticCompositionLocalOf { LightColors }

object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current
}

enum class ThemeMode { SYSTEM, LIGHT, DARK }

object ThemeManager {
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }
}

@Composable
fun KalkyTheme(
    content: @Composable () -> Unit
) {
    val themeMode by ThemeManager.themeMode.collectAsState()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colors = if (isDark) DarkColors else LightColors
    val materialColorScheme = if (isDark) darkColorScheme() else lightColorScheme()

    val strings = rememberStrings()
    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalStrings provides strings
    ) {
        MaterialTheme(colorScheme = materialColorScheme) {
            content()
        }
    }
}
