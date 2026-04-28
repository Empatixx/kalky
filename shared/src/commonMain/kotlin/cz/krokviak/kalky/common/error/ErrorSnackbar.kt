package cz.krokviak.kalky.common.error

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorSnackbarHost(
    error: UiError?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hostState = remember { SnackbarHostState() }
    LaunchedEffect(error) {
        if (error != null) {
            hostState.showSnackbar(error.toMessage())
            onDismiss()
        }
    }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        SnackbarHost(
            hostState = hostState,
            modifier = Modifier.padding(16.dp),
        ) { data ->
            Snackbar(snackbarData = data)
        }
    }
}
