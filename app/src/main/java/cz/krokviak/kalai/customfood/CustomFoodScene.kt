package cz.krokviak.kalai.customfood

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalai.common.entities.FoodItemEntity
import cz.krokviak.kalai.home.components.CaloriesRow
import cz.krokviak.kalai.home.components.FoodItemImage
import cz.krokviak.kalai.home.components.NutrientsRow
import cz.krokviak.kalai.i18n.LocalStrings
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.ui.components.KalaiButton
import cz.krokviak.kalai.ui.components.KalaiCard
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFoodScene(
    viewModel: CustomFoodViewModel,
    onBackClick: () -> Unit,
    onAddNewClick: () -> Unit,
    onFoodAdded: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val s = LocalStrings.current

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    LaunchedEffect(Unit) {
        viewModel.foodAdded.collect {
            onFoodAdded()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = s.customFood.title,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = s.common.back,
                            tint = AppTheme.colors.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = s.customFood.searchPlaceholder,
                            color = AppTheme.colors.onBackgroundSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = AppTheme.colors.onBackgroundSecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppTheme.colors.onBackgroundSecondary,
                        unfocusedBorderColor = AppTheme.colors.border,
                        focusedContainerColor = AppTheme.colors.surface,
                        unfocusedContainerColor = AppTheme.colors.surface,
                        focusedTextColor = AppTheme.colors.onBackground,
                        unfocusedTextColor = AppTheme.colors.onBackground
                    ),
                    singleLine = true
                )

                KalaiButton(
                    onClick = onAddNewClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = s.customFood.addNew,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                val hasAnyResults = uiState.historyItems.isNotEmpty() || uiState.apiResults.isNotEmpty()

                if (!hasAnyResults && !uiState.isLoading && uiState.searchQuery.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = s.customFood.noResults,
                            color = AppTheme.colors.onBackgroundSecondary,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(
                            bottom = if (uiState.selectedItems.isNotEmpty()) 80.dp else 16.dp
                        )
                    ) {
                        if (uiState.historyItems.isNotEmpty()) {
                            item {
                                Text(
                                    text = s.customFood.recentlyUsed,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppTheme.colors.onBackgroundSecondary
                                )
                            }
                            items(uiState.historyItems, key = { it.id }) { item ->
                                HistoryFoodItem(
                                    item = item,
                                    isSelected = item.id in uiState.selectedItems,
                                    onClick = { viewModel.toggleSelection(item.id) }
                                )
                            }
                        }
                        if (uiState.apiResults.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = s.customFood.onlineResults,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppTheme.colors.onBackgroundSecondary
                                )
                            }
                            items(uiState.apiResults.size, key = { "api_$it" }) { index ->
                                val product = uiState.apiResults[index]
                                ApiResultItem(
                                    product = product,
                                    onClick = { viewModel.addFromApi(product) }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (uiState.selectedItems.isNotEmpty()) {
            KalaiButton(
                onClick = { viewModel.addSelectedFoods() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "${s.common.add} (${uiState.selectedItems.size})",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun HistoryFoodItem(
    item: FoodItemEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    KalaiCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = AppTheme.colors.onBackground,
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(0.dp),
        color = AppTheme.colors.surfaceSecondary
    ) {
        Box {
            Row(modifier = Modifier.fillMaxWidth()) {
                FoodItemImage(foodItem = item, showBadge = false)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = item.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppTheme.colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CaloriesRow(calories = item.calories)
                    Spacer(modifier = Modifier.height(8.dp))
                    NutrientsRow(
                        protein = item.protein,
                        carbs = item.carbs,
                        fat = item.fat
                    )
                }
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .background(
                            color = AppTheme.colors.onBackground,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = AppTheme.colors.background,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ApiResultItem(
    product: OpenFoodFactsProduct,
    onClick: () -> Unit
) {
    val s = LocalStrings.current
    val name = product.productName?.takeIf { it.isNotBlank() } ?: s.common.unknownProduct
    val nutrients = product.nutriments
    val calories = nutrients?.energyKcal100g?.roundToInt() ?: 0
    val protein = nutrients?.proteins100g?.roundToInt() ?: 0
    val carbs = nutrients?.carbohydrates100g?.roundToInt() ?: 0
    val fat = nutrients?.fat100g?.roundToInt() ?: 0

    KalaiCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = AppTheme.colors.surfaceSecondary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = AppTheme.colors.onBackground,
                    maxLines = 2
                )
                Text(
                    text = "$calories kcal  |  ${s.common.protein}: ${protein}g  ${s.common.carbs}: ${carbs}g  ${s.common.fat}: ${fat}g",
                    fontSize = 13.sp,
                    color = AppTheme.colors.onBackgroundSecondary
                )
                Text(
                    text = s.customFood.per100g,
                    fontSize = 11.sp,
                    color = AppTheme.colors.onBackgroundSecondary
                )
            }
            AddButton()
        }
    }
}

@Composable
private fun AddButton() {
    val s = LocalStrings.current
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                color = AppTheme.colors.onBackground,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = s.common.add,
            tint = AppTheme.colors.background,
            modifier = Modifier.size(20.dp)
        )
    }
}
