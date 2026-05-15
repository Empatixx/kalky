package cz.krokviak.kalky.scenes.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import cz.krokviak.kalky.core.i18n.CzechStrings
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.core.theme.LightColors
import cz.krokviak.kalky.core.theme.LocalAppColors
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@OptIn(ExperimentalTestApi::class)
class HomeSceneTest {

    @Composable
    private fun viewport(content: @Composable () -> Unit) {
        CompositionLocalProvider(
            LocalAppColors provides LightColors,
            LocalStrings provides CzechStrings,
        ) {
            Box(modifier = Modifier.size(412.dp, 1500.dp)) { content() }
        }
    }

    @Test
    fun emptyState_rendersTitleAndSubtitle() = runComposeUiTest {
        setContent {
            viewport {
                HomeScene(
                    uiState = MainUiState(),
                    onFoodClick = {}, onFoodLongClick = {},
                    onDateChange = {}, onTodayClick = {},
                    onAddCustomClick = {}, onEditTargetsClick = {},
                    onSelectionClear = {}, onSaveSelectionAsCustom = {},
                    onDeleteSelection = {}, onDismissError = {},
                )
            }
        }

        onNodeWithText(CzechStrings.home.emptyTitle).assertExists()
        onNodeWithText(CzechStrings.home.emptySubtitle).assertExists()
    }

    @Test
    fun rendersAddedTodayHeader() = runComposeUiTest {
        setContent {
            viewport {
                HomeScene(
                    uiState = MainUiState(),
                    onFoodClick = {}, onFoodLongClick = {},
                    onDateChange = {}, onTodayClick = {},
                    onAddCustomClick = {}, onEditTargetsClick = {},
                    onSelectionClear = {}, onSaveSelectionAsCustom = {},
                    onDeleteSelection = {}, onDismissError = {},
                )
            }
        }
        onNodeWithText(CzechStrings.home.addedToday).assertExists()
    }
}
