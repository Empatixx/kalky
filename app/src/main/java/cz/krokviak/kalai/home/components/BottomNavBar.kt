package cz.krokviak.kalai.home.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import cz.krokviak.kalai.i18n.LocalStrings
import cz.krokviak.kalai.theme.AppTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.LocalContentColor

@Composable
fun BottomNavBar(
    currentPage: Int,
    onSceneSelected: (Int) -> Unit,
    onCameraClick: () -> Unit
) {
    val navColors = KalaiNavigationBarDefaults.itemColors(
        selectedIconColor = AppTheme.colors.onBackground,
        selectedTextColor = AppTheme.colors.onBackground,
        unselectedIconColor = AppTheme.colors.onBackgroundSecondary,
        unselectedTextColor = AppTheme.colors.onBackgroundSecondary,
        disabledIconColor = AppTheme.colors.onBackgroundSecondary,
        disabledTextColor = AppTheme.colors.onBackgroundSecondary
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side
            val s = LocalStrings.current
            NavItem(0, Icons.Outlined.Home, s.nav.home, currentPage, onSceneSelected, navColors)
            NavItem(1, Icons.Outlined.Analytics, s.nav.analytics, currentPage, onSceneSelected, navColors)

            // Center spacer for the FABs
            Box(modifier = Modifier.weight(1.2f))

            // Right side
            NavItem(2, Icons.Outlined.Person, s.nav.profile, currentPage, onSceneSelected, navColors)
            NavItem(3, Icons.Outlined.Settings, s.nav.settings, currentPage, onSceneSelected, navColors)
        }

        // Centered FAB overlapping the nav bar
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-8).dp),
            verticalAlignment = Alignment.Bottom
        ) {
            FloatingActionButton(
                onClick = onCameraClick,
                containerColor = AppTheme.colors.primary,
                shape = CircleShape,
                modifier = Modifier.size(64.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                contentColor = AppTheme.colors.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoCamera,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
private fun RowScope.NavItem(
    page: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    currentPage: Int,
    onSceneSelected: (Int) -> Unit,
    colors: KalaiNavigationBarItemColors
) {
    KalaiNavigationBarItem(
        selected = page == currentPage,
        onClick = { onSceneSelected(page) },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        },
        label = { Text(text = label, fontWeight = FontWeight.Bold) },
        alwaysShowLabel = true,
        colors = colors
    )
}

@Composable
fun RowScope.KalaiNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
    pressIndicationEnabled: Boolean = false,
    colors: KalaiNavigationBarItemColors = KalaiNavigationBarDefaults.itemColors(),
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
            value = MaterialTheme.typography.labelSmall
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
class KalaiNavigationBarItemColors internal constructor(
    private val selectedIconColor: Color,
    private val selectedTextColor: Color,
    private val unselectedIconColor: Color,
    private val unselectedTextColor: Color,
    private val disabledIconColor: Color,
    private val disabledTextColor: Color,
) {
    @Composable
    internal fun iconColor(selected: Boolean, enabled: Boolean): Color {
        return when {
            !enabled -> disabledIconColor
            selected -> selectedIconColor
            else -> unselectedIconColor
        }
    }

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
        if (other == null || other !is KalaiNavigationBarItemColors) return false

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

@Immutable
object KalaiNavigationBarDefaults {

    @Composable
    fun itemColors(
        selectedIconColor: Color = AppTheme.colors.onBackground,
        selectedTextColor: Color = AppTheme.colors.onBackground,
        unselectedIconColor: Color = AppTheme.colors.onBackgroundSecondary,
        unselectedTextColor: Color = AppTheme.colors.onBackgroundSecondary,
        disabledIconColor: Color = AppTheme.colors.onBackgroundSecondary,
        disabledTextColor: Color = AppTheme.colors.onBackgroundSecondary,
    ) = KalaiNavigationBarItemColors(
        selectedIconColor = selectedIconColor,
        selectedTextColor = selectedTextColor,
        unselectedIconColor = unselectedIconColor,
        unselectedTextColor = unselectedTextColor,
        disabledIconColor = disabledIconColor,
        disabledTextColor = disabledTextColor
    )
}
