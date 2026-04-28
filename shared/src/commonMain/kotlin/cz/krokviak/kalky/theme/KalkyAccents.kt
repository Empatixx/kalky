package cz.krokviak.kalky.theme

import androidx.compose.ui.graphics.Color

/**
 * Brand and accent colors that aren't part of the AppColors palette.
 * Kept separate so they can be used outside of CompositionLocal contexts
 * (e.g. in static `val` initializers).
 */
object KalkyAccents {
    // iOS-style accent (used as the active-state highlight on AnalyticsScene date pickers).
    val iosRed = Color(0xFFFF3B30)

    // Generic accent blue — used for "Storage" / barcode-source icons in CustomFoodScene
    // and as a non-theme highlight where a cool color is desired.
    val accentBlue = Color(0xFF4A90D9)

    // Decorative brand gradient stops (used in KalkyGradientBackground).
    val brandSky = Color(0xFF79C8FF)
    val brandViolet = Color(0xFF9A63FF)
    val brandPink = Color(0xFFFF78D8)

    // BMI category colors (under / normal / over / obese).
    val bmiUnder = Color(0xFF4DA3FF)
    val bmiNormal = Color(0xFF4ABF63)
    val bmiOver = Color(0xFFE3BE47)
    val bmiObese = Color(0xFFE98635)
}
