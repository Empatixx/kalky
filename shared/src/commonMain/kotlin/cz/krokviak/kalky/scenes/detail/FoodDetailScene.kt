package cz.krokviak.kalky.scenes.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import cz.krokviak.kalky.scenes.detail.components.FoodBottomSheetCard
import cz.krokviak.kalky.core.i18n.rememberStrings
import cz.krokviak.kalky.core.ui.LocalDimensions
import cz.krokviak.kalky.core.ui.components.KalkyContextMenu
import cz.krokviak.kalky.core.ui.components.KalkyContextMenuItem

@Composable
fun FoodDetailScene(
    foodDetailViewModel: FoodDetailViewModel,
    uiState: FoodDetailState,
    foodId: Long,
    onExitClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Main Image
        AsyncImage(
            model = uiState.localImagePath ?: "",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.52f)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )

        val dims = LocalDimensions.current
        // Top Icons with context menu
        FoodDetailTopIcons(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = dims.screenPadding, start = dims.screenPadding, end = dims.screenPadding),
            onExitClick = onExitClick,
            onShareClick = onShareClick,
            onDeleteClick = onDeleteClick
        )

        // Bottom Sheet
        FoodBottomSheetCard(
            name = uiState.name,
            calories = uiState.calories,
            protein = uiState.protein,
            fats = uiState.fat,
            carbs = uiState.carbs,
            activeField = uiState.activeField,
            onProteinChange = foodDetailViewModel::onProteinChange,
            onCarbsChange = foodDetailViewModel::onCarbsChange,
            onFatChange = foodDetailViewModel::onFatChange,
            onToggleField = foodDetailViewModel::toggleField,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.BottomCenter),
            onFinish = {
                foodDetailViewModel.finish()
                onExitClick()
            },
            onFixResult = { foodDetailViewModel.fixResult() }
        )
        cz.krokviak.kalky.core.common.error.ErrorSnackbarHost(
            error = uiState.error,
            onDismiss = foodDetailViewModel::dismissError,
        )
    }
}

@Composable
fun FoodDetailTopIcons(
    modifier: Modifier = Modifier,
    onExitClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val strings = rememberStrings()
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Exit Button with translucent circle
        IconButton(
            onClick = onExitClick,
            modifier = Modifier
                .background(
                    color = Color.Gray.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = strings.common.cdExit,
                tint = Color.White
            )
        }

        // 3 dots button - opens context menu
        Box {
            IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier
                    .background(
                        color = Color.Gray.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = strings.common.cdMore,
                    tint = Color.White
                )
            }

            KalkyContextMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                items = listOf(
                    KalkyContextMenuItem(
                        label = strings.detail.share,
                        icon = Icons.Default.Share,
                        onClick = onShareClick
                    ),
                    KalkyContextMenuItem(
                        label = strings.detail.delete,
                        icon = Icons.Default.Delete,
                        isDestructive = true,
                        onClick = onDeleteClick
                    )
                )
            )
        }
    }
}
