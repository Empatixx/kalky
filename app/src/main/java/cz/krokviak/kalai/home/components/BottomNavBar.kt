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

@Composable
fun BottomNavBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        Icons.Outlined.Home to "Výchozí",
        Icons.Outlined.Analytics to "Analýza",
        Icons.Outlined.Settings to "Nastavení"
    )

    NavigationBar(modifier = Modifier) {
        Spacer(modifier = Modifier.width(8.dp)) // push items to the right

        items.forEachIndexed { index, (icon, label) ->
            val alphaValue = if (selectedItem == index) 1f else 0.5f
            NavigationBarItem(
                selected = (selectedItem == index),
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
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
