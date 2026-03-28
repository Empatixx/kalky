package cz.krokviak.kalai.auth

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

    var showEmailForm by remember { mutableStateOf(false) }
    var isRegisterMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                    // Google Sign-In
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Apple Sign-In
                    KalaiButton(
                        onClick = {
                            authViewModel.signInWithApple(context as Activity)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = AppTheme.colors.onBackground,
                        contentColor = AppTheme.colors.background
                    ) {
                        Text(text = s.auth.continueWithApple, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Or divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = AppTheme.colors.border)
                        Text(
                            text = s.auth.or,
                            color = AppTheme.colors.onBackgroundSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = AppTheme.colors.border)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email toggle
                    AnimatedVisibility(visible = !showEmailForm) {
                        KalaiButton(
                            onClick = { showEmailForm = true },
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = AppTheme.colors.surface,
                            contentColor = AppTheme.colors.onBackground
                        ) {
                            Text(text = s.auth.continueWithEmail, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Email/Password form
                    AnimatedVisibility(visible = showEmailForm) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text(s.auth.email) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AppTheme.colors.onBackground,
                                    unfocusedBorderColor = AppTheme.colors.border,
                                    focusedLabelColor = AppTheme.colors.onBackground,
                                    unfocusedLabelColor = AppTheme.colors.onBackgroundSecondary,
                                    cursorColor = AppTheme.colors.onBackground,
                                    focusedTextColor = AppTheme.colors.onBackground,
                                    unfocusedTextColor = AppTheme.colors.onBackground
                                )
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text(s.auth.password) },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AppTheme.colors.onBackground,
                                    unfocusedBorderColor = AppTheme.colors.border,
                                    focusedLabelColor = AppTheme.colors.onBackground,
                                    unfocusedLabelColor = AppTheme.colors.onBackgroundSecondary,
                                    cursorColor = AppTheme.colors.onBackground,
                                    focusedTextColor = AppTheme.colors.onBackground,
                                    unfocusedTextColor = AppTheme.colors.onBackground
                                )
                            )

                            KalaiButton(
                                onClick = {
                                    authViewModel.signInWithEmail(email, password, isRegisterMode)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                containerColor = AppTheme.colors.onBackground,
                                contentColor = AppTheme.colors.background
                            ) {
                                Text(
                                    text = if (isRegisterMode) s.auth.createAccount else s.auth.signIn,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isRegisterMode) s.auth.haveAccount else s.auth.noAccount,
                                    color = AppTheme.colors.onBackgroundSecondary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isRegisterMode) s.auth.signIn else s.auth.createAccount,
                                    color = AppTheme.colors.onBackground,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.clickable {
                                        isRegisterMode = !isRegisterMode
                                        authViewModel.clearError()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
