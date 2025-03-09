package cz.krokviak.kalai.home.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.krokviak.kalai.home.Scene

@Composable
fun BottomNavBar(
    currentScene: Scene,
    onSceneSelected: (Scene) -> Unit
) {
    // A list pairing each Scene with its associated icon and label
    val sceneItems = listOf(
        Scene.HOME to (Icons.Outlined.Home to "Výchozí"),
        Scene.ANALYTICS to (Icons.Outlined.Analytics to "Analýza"),
        Scene.SETTINGS to (Icons.Outlined.Settings to "Nastavení")
    )

    NavigationBar {
        // Spacer to push items slightly to the right, if desired
        Spacer(modifier = Modifier.width(8.dp))

        sceneItems.forEach { (scene, iconLabelPair) ->
            val (icon, label) = iconLabelPair
            val isSelected = (scene == currentScene)
            val alphaValue = if (isSelected) 1f else 0.5f

            NavigationBarItem(
                selected = isSelected,
                onClick = { onSceneSelected(scene) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier
                            .size(24.dp)
                            .alpha(alphaValue)
                    )
                },
                label = {
                    Text(
                        text = label,
                        modifier = Modifier.alpha(alphaValue)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }

        // Spacer with weight(1f) to consume leftover space
        Spacer(modifier = Modifier.weight(1f))
    }
}
