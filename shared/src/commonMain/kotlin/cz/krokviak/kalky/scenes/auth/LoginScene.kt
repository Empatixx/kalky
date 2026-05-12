package cz.krokviak.kalky.scenes.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.core.ui.LocalDimensions
import cz.krokviak.kalky.core.ui.components.KalkyButton
import cz.krokviak.kalky.core.ui.components.KalkyGradientBackground
import cz.krokviak.kalky.core.ui.components.states.LoadingState

@Composable
fun LoginScene(
    onSignInWithGoogle: () -> Unit,
    onSignInWithApple: () -> Unit = {},
    onSignInSuccess: () -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    isSignedIn: Boolean = false
) {
    val s = LocalStrings.current
    val dims = LocalDimensions.current

    LaunchedEffect(isSignedIn) {
        if (isSignedIn) {
            onSignInSuccess()
        }
    }

    KalkyGradientBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(dims.screenPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = s.auth.loginTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppTheme.colors.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = s.auth.loginSubtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppTheme.colors.onBackgroundSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                if (isLoading) {
                    LoadingState()
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (!isLoading) {
                    KalkyButton(
                        onClick = { onSignInWithGoogle() },
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = AppTheme.colors.onBackground,
                        contentColor = AppTheme.colors.background
                    ) {
                        Text(text = s.auth.continueWithGoogle, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
