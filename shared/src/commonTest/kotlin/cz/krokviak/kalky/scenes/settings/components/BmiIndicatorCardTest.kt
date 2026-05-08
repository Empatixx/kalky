package cz.krokviak.kalky.scenes.settings.components

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import cz.krokviak.kalky.core.i18n.CzechStrings
import cz.krokviak.kalky.core.i18n.EnglishStrings
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.core.theme.LightColors
import cz.krokviak.kalky.core.theme.LocalAppColors
import kotlin.test.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

// Compose UI tests on the Android target need a fake Android framework.
// Robolectric provides Build.FINGERPRINT and the SDK stubs the renderer needs.
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@OptIn(ExperimentalTestApi::class)
class BmiIndicatorCardTest {

    @Test
    fun underweight_displaysCzechLabel() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(
                LocalAppColors provides LightColors,
                LocalStrings provides CzechStrings,
            ) {
                BmiIndicatorCard(bmi = 17f)
            }
        }
        onNodeWithText("Podváha").assertIsDisplayed()
        onNodeWithText("BMI").assertIsDisplayed()
        onNodeWithText("17.0").assertIsDisplayed()
    }

    @Test
    fun normal_displaysCzechLabel() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(
                LocalAppColors provides LightColors,
                LocalStrings provides CzechStrings,
            ) {
                BmiIndicatorCard(bmi = 22f)
            }
        }
        onNodeWithText("Normální").assertIsDisplayed()
    }

    @Test
    fun overweight_displaysCzechLabel() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(
                LocalAppColors provides LightColors,
                LocalStrings provides CzechStrings,
            ) {
                BmiIndicatorCard(bmi = 27f)
            }
        }
        onNodeWithText("Nadváha").assertIsDisplayed()
    }

    @Test
    fun obese_displaysCzechLabel() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(
                LocalAppColors provides LightColors,
                LocalStrings provides CzechStrings,
            ) {
                BmiIndicatorCard(bmi = 35f)
            }
        }
        onNodeWithText("Obezita").assertIsDisplayed()
    }

    @Test
    fun englishLocale_displaysEnglishLabels() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(
                LocalAppColors provides LightColors,
                LocalStrings provides EnglishStrings,
            ) {
                BmiIndicatorCard(bmi = 22f)
            }
        }
        onNodeWithText("Normal").assertIsDisplayed()
    }
}
