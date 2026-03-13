package cz.krokviak.kalai.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.krokviak.kalai.R
import cz.krokviak.kalai.i18n.LocalStrings
import cz.krokviak.kalai.theme.AppTheme

@Composable
fun FoodDetailButtons(
    onFinish: () -> Unit,
    onFixResult: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = AppTheme.colors.surface),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FixResultsButton(
            onClick = onFixResult,
            modifier = Modifier
                .padding(16.dp)
                .weight(0.5f)
        )

        DoneButton(
            onClick = onFinish,
            modifier = Modifier
                .padding(16.dp)
                .weight(0.5f)
        )

    }

}

@Composable
fun FixResultsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(40),
        colors = ButtonColors(
            contentColor = AppTheme.colors.onBackground,
            containerColor = AppTheme.colors.surface,
            disabledContainerColor = AppTheme.colors.primary,
            disabledContentColor = AppTheme.colors.onPrimary,
        ),
        border = BorderStroke(1.dp, AppTheme.colors.primary),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.iconai),
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp),
            contentDescription = null
        )
        Text(
            text = LocalStrings.current.detail.fix,
            color = AppTheme.colors.onBackground,
        )
    }
}

@Composable
fun DoneButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(40),
        colors = ButtonColors(
            contentColor = AppTheme.colors.onPrimary,
            containerColor = AppTheme.colors.primary,
            disabledContainerColor = AppTheme.colors.primary,
            disabledContentColor = AppTheme.colors.onPrimary,
        )
    ) {
        Text(
            text = LocalStrings.current.detail.done,
            color = AppTheme.colors.onPrimary,
        )
    }
}
