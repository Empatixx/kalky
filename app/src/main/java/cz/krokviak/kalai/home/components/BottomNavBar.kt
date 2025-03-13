package cz.krokviak.kalai.home.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.LocalContentColor
import io.github.alexzhirkevich.cupertino.CupertinoBottomSheetContent
import io.github.alexzhirkevich.cupertino.CupertinoBottomSheetScaffold
import io.github.alexzhirkevich.cupertino.CupertinoDivider
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.CupertinoNavigationBar
import io.github.alexzhirkevich.cupertino.CupertinoScaffold
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.ProvideTextStyle
import io.github.alexzhirkevich.cupertino.cupertinoTranslucentTopBarColor
import io.github.alexzhirkevich.cupertino.theme.CupertinoTheme

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun BottomNavBar(
    currentPage: Int,
    onSceneSelected: (Int) -> Unit
) {
    val sceneItems = listOf(
        0 to (Icons.Outlined.Home to "Domov"),
        1 to (Icons.Outlined.Analytics to "Analýza"),
        2 to (Icons.Outlined.Settings to "Nastavení")
    )

    CupertinoNavigationBar(
        isTranslucent = true,            // turn off translucency if you want solid black
        isTransparent = true,
        modifier = Modifier
            .height(75.dp)
    ) {
        sceneItems.forEach { (scene, iconLabelPair) ->
            val (icon, label) = iconLabelPair
            val isSelected = (scene == currentPage)

            CupertinoNavigationBarItem(
                selected = isSelected,
                onClick = { onSceneSelected(scene) },
                icon = {
                    CupertinoIcon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp) // optionally match size or omit
                    )
                },
                label = {
                    CupertinoText(
                        text = label,
                        fontWeight = FontWeight.Bold
                    )
                },
                // Show label always or only if selected
                alwaysShowLabel = true,
                // Override default “blue” colors with your own
                colors = CupertinoNavigationBarDefaults.itemColors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = Color.Black,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    // Optional overrides for disabled state if needed
                    disabledIconColor = Color.DarkGray,
                    disabledTextColor = Color.DarkGray
                )
            )
        }
        CupertinoNavigationBarItem(
            selected = false,
            onClick = { /* No-op */ },
            icon = {},
            label = {},
            alwaysShowLabel = false,
            colors = CupertinoNavigationBarDefaults.itemColors(
                selectedIconColor = Color.Transparent,
                unselectedIconColor = Color.Transparent
            )
        )

    }
}

@Composable
@ExperimentalCupertinoApi
fun RowScope.CupertinoNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
    pressIndicationEnabled: Boolean = false,
    colors: CupertinoNavigationBarItemColors = CupertinoNavigationBarDefaults.itemColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {

    val pressed by interactionSource.collectIsPressedAsState()

    Column(
        modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null
            )
            .weight(1f)
            .padding(top = 6.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        val iconColor = colors.iconColor(selected, enabled)
        val textColor = colors.textColor(selected, enabled)

        ProvideTextStyle(
            value = CupertinoTheme.typography.caption2
        ) {
            val alpha = if (pressIndicationEnabled && pressed && !selected)
                textColor.alpha * .33f
            else textColor.alpha

            CompositionLocalProvider(
                LocalContentColor provides iconColor.copy(alpha = alpha)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                if (label != null && (alwaysShowLabel || selected)) {
                    label()
                }
            }
        }
    }
}

@Stable
@ExperimentalCupertinoApi
class CupertinoNavigationBarItemColors internal constructor(
    private val selectedIconColor: Color,
    private val selectedTextColor: Color,
    private val unselectedIconColor: Color,
    private val unselectedTextColor: Color,
    private val disabledIconColor: Color,
    private val disabledTextColor: Color,
) {
    /**
     * Represents the icon color for this item, depending on whether it is [selected].
     *
     * @param selected whether the item is selected
     * @param enabled whether the item is enabled
     */
    @Composable
    internal fun iconColor(selected: Boolean, enabled: Boolean): Color {
        return when {
            !enabled -> disabledIconColor
            selected -> selectedIconColor
            else -> unselectedIconColor
        }
    }

    /**
     * Represents the text color for this item, depending on whether it is [selected].
     *
     * @param selected whether the item is selected
     * @param enabled whether the item is enabled
     */
    @Composable
    internal fun textColor(selected: Boolean, enabled: Boolean): Color {
        return when {
            !enabled -> disabledTextColor
            selected -> selectedTextColor
            else -> unselectedTextColor
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is CupertinoNavigationBarItemColors) return false

        if (selectedIconColor != other.selectedIconColor) return false
        if (unselectedIconColor != other.unselectedIconColor) return false
        if (selectedTextColor != other.selectedTextColor) return false
        if (unselectedTextColor != other.unselectedTextColor) return false
        if (disabledIconColor != other.disabledIconColor) return false
        return disabledTextColor == other.disabledTextColor
    }

    override fun hashCode(): Int {
        var result = selectedIconColor.hashCode()
        result = 31 * result + unselectedIconColor.hashCode()
        result = 31 * result + selectedTextColor.hashCode()
        result = 31 * result + unselectedTextColor.hashCode()
        result = 31 * result + disabledIconColor.hashCode()
        result = 31 * result + disabledTextColor.hashCode()

        return result
    }
}

@ExperimentalCupertinoApi
@Immutable
object CupertinoNavigationBarDefaults {

    /**
     * Default container color of the [CupertinoNavigationBar]
     *
     * Note: navigation bar itself does not produce cupertino thin material glass effect.
     * This effect works only inside [CupertinoScaffold], [CupertinoBottomSheetScaffold], [CupertinoBottomSheetContent].
     * To achieve this effect with custom top app bar use [cupertinoTranslucentTopBarColor]
     * function that will communicate with scaffold and return either
     * [Color.Transparent] if color was successfully applied to scaffold (and top bar itself
     * should be transparent) or passed color if scaffold wasn't found.
     * */
    val containerColor: Color
        @Composable
        @ReadOnlyComposable
        get() = CupertinoTheme.colorScheme.tertiarySystemBackground

    @Composable
    @ReadOnlyComposable
    fun itemColors(
        selectedIconColor: Color = CupertinoTheme.colorScheme.accent,
        selectedTextColor: Color = CupertinoTheme.colorScheme.accent,
        unselectedIconColor: Color = CupertinoTheme.colorScheme.secondaryLabel,
        unselectedTextColor: Color = CupertinoTheme.colorScheme.secondaryLabel,
        disabledIconColor: Color = CupertinoTheme.colorScheme.tertiaryLabel,
        disabledTextColor: Color = CupertinoTheme.colorScheme.tertiaryLabel,
    ) = CupertinoNavigationBarItemColors(
        selectedIconColor = selectedIconColor,
        selectedTextColor = selectedTextColor,
        unselectedIconColor = unselectedIconColor,
        unselectedTextColor = unselectedTextColor,
        disabledIconColor = disabledIconColor,
        disabledTextColor = disabledTextColor
    )

    @Composable
    fun divider() {
        CupertinoDivider()
    }
}

internal object CupertinoNavigationBarTokens {
    val Height = 49.dp
}
/*
        sceneItems.forEach { (scene, iconLabelPair) ->
            val (icon, label) = iconLabelPair
            val isSelected = (scene == currentScene)

            CupertinoNavigationBarItem(
                selected = isSelected,
                onClick = { onSceneSelected(scene) },
                icon = {
                    CupertinoIcon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(64.dp) // optionally match size or omit
                    )
                },
                label = {
                    CupertinoText(
                        text = label
                    )
                },
                // Show label always or only if selected
                alwaysShowLabel = true,
                // Override default “blue” colors with your own
                colors = CupertinoNavigationBarDefaults.itemColors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = Color.Black,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    // Optional overrides for disabled state if needed
                    disabledIconColor = Color.DarkGray,
                    disabledTextColor = Color.DarkGray
                )
            )
        }
        CupertinoNavigationBarItem(
            selected = false,
            onClick = { /* No-op */ },
            icon = {},
            label = {},
            alwaysShowLabel = false,
            colors = CupertinoNavigationBarDefaults.itemColors(
                selectedIconColor = Color.Transparent,
                unselectedIconColor = Color.Transparent
            )
        )
 */