package cz.krokviak.kalky.core.theme

import androidx.compose.ui.graphics.Color

/**
 * Brand and accent colors that aren't part of the AppColors palette.
 * Kept separate so they can be used outside of CompositionLocal contexts
 * (e.g. in static `val` initializers).
 */
object KalkyAccents {
    val iosRed = Color(0xFFFF3B30)
    val accentBlue = Color(0xFF4A90D9)

    val brandSky = Color(0xFF79C8FF)
    val brandViolet = Color(0xFF9A63FF)
    val brandPink = Color(0xFFFF78D8)

    val bmiUnder = Color(0xFF4DA3FF)
    val bmiNormal = Color(0xFF4ABF63)
    val bmiOver = Color(0xFFE3BE47)
    val bmiObese = Color(0xFFE98635)
}
