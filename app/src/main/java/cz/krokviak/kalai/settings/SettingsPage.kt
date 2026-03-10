package cz.krokviak.kalai.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.theme.ThemeManager
import cz.krokviak.kalai.theme.ThemeMode
import io.github.alexzhirkevich.cupertino.CupertinoSegmentedControl
import io.github.alexzhirkevich.cupertino.CupertinoSegmentedControlIndicator
import io.github.alexzhirkevich.cupertino.CupertinoSegmentedControlTab
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun SettingsPage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CupertinoText(
            text = "Nastavení",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = AppTheme.colors.onBackground
        )

        // Theme section
        SectionHeader("Vzhled")

        val themeMode by ThemeManager.themeMode.collectAsState()
        val themeLabels = listOf("Systém", "Světlý", "Tmavý")
        val themeModes = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)

        CupertinoSegmentedControl(
            selectedTabIndex = themeModes.indexOf(themeMode),
            modifier = Modifier.fillMaxWidth(),
            indicator = { tabPositions ->
                CupertinoSegmentedControlIndicator(
                    selectedTabIndex = themeModes.indexOf(themeMode),
                    tabPositions = tabPositions
                )
            },
            tabs = {
                themeLabels.forEachIndexed { index, label ->
                    CupertinoSegmentedControlTab(
                        onClick = { ThemeManager.setThemeMode(themeModes[index]) },
                        isSelected = themeMode == themeModes[index]
                    ) {
                        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Account section placeholder
        SectionHeader("Účet")

        CupertinoText(
            text = "Verze aplikace 1.0",
            color = AppTheme.colors.onBackgroundSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SectionHeader(title: String) {
    CupertinoText(
        text = title,
        color = AppTheme.colors.onBackgroundSecondary,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp)
    )
}
