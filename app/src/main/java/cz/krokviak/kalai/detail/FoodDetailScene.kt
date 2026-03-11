package cz.krokviak.kalai.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import cz.krokviak.kalai.detail.components.FoodBottomSheetCard

@Composable
fun FoodDetailScene(
    foodDetailViewModel: FoodDetailViewModel,
    uiState: FoodDetailState,
    foodId: Long,
    onExitClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Main Image
        AsyncImage(
            model = uiState.localImagePath ?: "",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(
                    min = 0.dp,
                    max = LocalConfiguration.current.screenHeightDp.dp - (500 - 25).dp
                ) // Dynamic height
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )

        // Refactored Top Icons
        FoodDetailTopIcons(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            onExitClick = onExitClick,
            onShareClick = onShareClick,
            onDeleteClick = {}
        )

        // Bottom Sheet
        FoodBottomSheetCard(
            name = uiState.name,
            portion = uiState.portion,
            calories = uiState.calories,
            protein = uiState.protein,
            fats = uiState.fat,
            carbs = uiState.carbs,
            healthScore = uiState.healthScore,
            onIncreasePortion = { foodDetailViewModel.increasePortion() },
            onDecreasePortion = { foodDetailViewModel.decreasePortion() },
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp) // Fixed height
                .align(Alignment.BottomCenter),
            onFinish = {
                foodDetailViewModel.finish()
                onExitClick()
            },
            onFixResult = { foodDetailViewModel.fixResult() }
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
                contentDescription = "Exit",
                tint = Color.White
            )
        }

        // 3 dots with Dropdown Menu
        // We use a Box as an anchor for the dropdown
        Box {
            val menuExpanded = remember { mutableStateOf(false) }

            IconButton(
                onClick = { menuExpanded.value = true },
                modifier = Modifier
                    .background(
                        color = Color.Gray.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }

            DropdownMenu(
                expanded = menuExpanded.value,
                onDismissRequest = { menuExpanded.value = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Share") },
                    onClick = {
                        onShareClick()
                        menuExpanded.value = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        onDeleteClick()
                        menuExpanded.value = false
                    }
                )
            }
        }
    }
}
