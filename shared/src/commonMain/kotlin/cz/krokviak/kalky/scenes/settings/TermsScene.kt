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
fun TermsScene(
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
                    text = s.legal.termsTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppTheme.colors.onBackground,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LegalSectionHeader("1. Úvodní ustanovení")
            LegalBody(
                "Tyto obchodní podmínky upravují práva a povinnosti uživatelů mobilní aplikace Kalky " +
                "(dále jen \u201EAplikace\u201C). Používáním Aplikace souhlasíte s těmito podmínkami."
            )

            LegalSectionHeader("2. Popis služby")
            LegalBody(
                "Aplikace Kalky slouží k sledování příjmu potravin a nutričních hodnot. " +
                "Aplikace umožňuje zaznamenávat jídla pomocí fotografií, čárových kódů nebo ručního zadání."
            )

            LegalSectionHeader("3. Uživatelský účet")
            LegalBody(
                "Pro používání Aplikace není vyžadována registrace. Veškerá data jsou ukládána lokálně " +
                "na zařízení uživatele."
            )

            LegalSectionHeader("4. Omezení odpovědnosti")
            LegalBody(
                "Nutriční hodnoty zobrazené v Aplikaci mají informativní charakter a nenahrazují odborné " +
                "poradenství. Provozovatel nenese odpovědnost za přesnost údajů získaných z externích databází."
            )

            LegalSectionHeader("5. Změny podmínek")
            LegalBody(
                "Provozovatel si vyhrazuje právo tyto podmínky kdykoli změnit. O změnách bude uživatel " +
                "informován prostřednictvím aktualizace Aplikace."
            )

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
