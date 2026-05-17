package cz.krokviak.kalky.scenes.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.core.ui.LocalDimensions
import cz.krokviak.kalky.core.ui.components.KalkyGradientBackground

@Composable
fun PrivacyPolicyScene(
    onBackClick: () -> Unit
) {
    val s = LocalStrings.current
    val dims = LocalDimensions.current

    KalkyGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(dims.screenPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = s.common.back,
                    tint = AppTheme.colors.onBackground,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onBackClick() }
                )
                Text(
                    text = s.legal.privacyTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppTheme.colors.onBackground,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            s.legal.privacySections.forEach { section ->
                LegalSectionHeader(section.header)
                LegalBody(section.body)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun LegalSectionHeader(title: String) {
    val dims = LocalDimensions.current
    Text(
        text = title,
        color = AppTheme.colors.onBackground,
        fontSize = dims.fontBody,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun LegalBody(text: String) {
    val dims = LocalDimensions.current
    Text(
        text = text,
        color = AppTheme.colors.onBackgroundSecondary,
        fontSize = dims.fontBody,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}
