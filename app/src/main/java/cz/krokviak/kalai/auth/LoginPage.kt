package cz.krokviak.kalai.auth

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.krokviak.kalai.i18n.LocalStrings
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.ui.LocalDimensions
import cz.krokviak.kalai.ui.components.KalaiButton
import cz.krokviak.kalai.ui.components.KalaiGradientBackground

@Composable
fun LoginPage(
    authViewModel: AuthViewModel,
    onSignInSuccess: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val s = LocalStrings.current
    val dims = LocalDimensions.current
    val context = LocalContext.current

    LaunchedEffect(uiState.isSignedIn) {
        if (uiState.isSignedIn) {
            onSignInSuccess()
        }
    }

    KalaiGradientBackground {
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

                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = AppTheme.colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (!uiState.isLoading) {
                    KalaiButton(
                        onClick = {
                            val activity = context as Activity
                            val webClientId = context.getString(cz.krokviak.kalai.R.string.default_web_client_id)
                            authViewModel.signInWithGoogle(activity, webClientId)
                        },
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
