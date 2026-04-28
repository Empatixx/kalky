package cz.krokviak.kalky.customfood

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
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Storage
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.common.error.ErrorSnackbarHost
import cz.krokviak.kalky.home.components.CaloriesRow
import cz.krokviak.kalky.home.components.FoodItemImage
import cz.krokviak.kalky.home.components.NutrientsRow
import cz.krokviak.kalky.i18n.LocalStrings
import cz.krokviak.kalky.nutrientedit.components.NutrientEditRow
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.theme.MacroColors
import cz.krokviak.kalky.ui.LocalDimensions
import cz.krokviak.kalky.ui.components.KalkyButton
import cz.krokviak.kalky.ui.components.KalkyCard
import cz.krokviak.kalky.ui.components.KalkySegmentedControl
import cz.krokviak.kalky.ui.components.states.EmptyState
import kotlinx.collections.immutable.persistentListOf
import kotlin.math.roundToInt

private val PORTION_PRESETS = persistentListOf(50, 100, 150, 200, 250)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFoodScene(
    uiState: CustomFoodUiState,
    foodAdded: kotlinx.coroutines.flow.SharedFlow<Long>,
    onBackClick: () -> Unit,
    onAddNewClick: () -> Unit,
    onFoodAdded: () -> Unit,
    onLoadHistory: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleSelection: (Long) -> Unit,
    onSelectApiProduct: (OpenFoodFactsProduct) -> Unit,
    onAddSelectedFoods: () -> Unit,
    onPortionChanged: (Int) -> Unit,
    onConfirmApiProduct: () -> Unit,
    onDismissPortionPicker: () -> Unit,
    onDismissError: () -> Unit,
) {
    val s = LocalStrings.current
    var selectedTab by remember { mutableStateOf(0) } // 0=All, 1=My Foods, 2=Recently Used

    LaunchedEffect(Unit) { onLoadHistory() }

    LaunchedEffect(Unit) {
        foodAdded.collect { onFoodAdded() }
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

            val dims = LocalDimensions.current
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dims.screenPadding),
                verticalArrangement = Arrangement.spacedBy(dims.itemSpacing)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
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

                KalkySegmentedControl(
                    selectedIndex = selectedTab,
                    items = listOf(s.customFood.all, s.customFood.myFoods, s.customFood.history),
                    onItemSelected = { selectedTab = it },
                    modifier = Modifier.fillMaxWidth(),
                    textSize = 13.sp
                )

                val hasAnyResults = when (selectedTab) {
                    1 -> true // Always show list so Add button is visible
                    2 -> uiState.historyItems.isNotEmpty()
                    else -> uiState.customFoods.isNotEmpty() || uiState.historyItems.isNotEmpty() || uiState.apiResults.isNotEmpty()
                }

                if (!hasAnyResults && !uiState.isLoading) {
                    EmptyState(title = s.customFood.noResults)
                } else {
                    val historyTop5 = remember(uiState.historyItems) {
                        uiState.historyItems.take(5)
                    }
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(
                            bottom = if (uiState.selectedItems.isNotEmpty()) 80.dp else 16.dp
                        )
                    ) {
                        if (selectedTab == 1) {
                            item {
                                KalkyCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable(onClick = onAddNewClick),
                                    shape = RoundedCornerShape(16.dp),
                                    color = AppTheme.colors.surfaceSecondary
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(
                                                    color = AppTheme.colors.onBackground,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                                tint = AppTheme.colors.background,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = s.customFood.addManually,
                                            fontSize = dims.fontBody,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AppTheme.colors.onBackground
                                        )
                                    }
                                }
                            }
                        }
                        if (uiState.customFoods.isNotEmpty() && selectedTab != 2) {
                            item {
                                Text(
                                    text = s.customFood.myFoods,
                                    fontSize = dims.fontBody,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppTheme.colors.onBackgroundSecondary
                                )
                            }
                            items(uiState.customFoods, key = { "custom_${it.id}" }) { item ->
                                HistoryFoodItem(
                                    item = item,
                                    isSelected = item.id in uiState.selectedItems,
                                    onClick = { onToggleSelection(item.id) }
                                )
                            }
                        }
                        if (uiState.historyItems.isNotEmpty() && selectedTab != 1) {
                            item {
                                SectionHeader(
                                    text = s.customFood.recentlyUsed,
                                    icon = Icons.Default.History,
                                    iconColor = Color(0xFF4A90D9)
                                )
                            }
                            items(historyTop5, key = { it.id }) { item ->
                                HistoryFoodItem(
                                    item = item,
                                    isSelected = item.id in uiState.selectedItems,
                                    onClick = { onToggleSelection(item.id) },
                                    fallbackTint = Color(0xFF4A90D9),
                                )
                            }
                        }
                        if (uiState.apiResults.isNotEmpty() && selectedTab == 0) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                SectionHeader(
                                    text = s.customFood.onlineResults,
                                    icon = Icons.Default.Storage,
                                    iconColor = AppTheme.colors.onBackgroundSecondary
                                )
                            }
                            items(
                                items = uiState.apiResults,
                                key = { product -> "api_${product.productName ?: product.hashCode()}" }
                            ) { product ->
                                ApiResultItem(
                                    product = product,
                                    onClick = { onSelectApiProduct(product) }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (uiState.selectedItems.isNotEmpty()) {
            KalkyButton(
                onClick = onAddSelectedFoods,
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
        ErrorSnackbarHost(error = uiState.error, onDismiss = onDismissError)
    }

    uiState.selectedApiProduct?.let { product ->
        PortionPickerSheet(
            product = product,
            portionGrams = uiState.portionGrams,
            onPortionChanged = onPortionChanged,
            onConfirm = onConfirmApiProduct,
            onDismiss = onDismissPortionPicker
        )
    }
}

@Composable
private fun HistoryFoodItem(
    item: FoodItemEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    fallbackTint: Color = AppTheme.colors.onBackgroundSecondary
) {
    val dims = LocalDimensions.current
    KalkyCard(
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
                FoodItemImage(foodItem = item, showBadge = false, fallbackTint = fallbackTint)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = item.name,
                        fontSize = dims.fontBody,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppTheme.colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(dims.halfSpacing))
                    CaloriesRow(calories = item.calories)
                    Spacer(modifier = Modifier.height(dims.halfSpacing))
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
    val dims = LocalDimensions.current
    val s = LocalStrings.current
    val name = product.productName?.takeIf { it.isNotBlank() } ?: s.common.unknownProduct
    val nutrients = product.nutriments
    val calories = nutrients?.energyKcal100g?.roundToInt() ?: 0
    val protein = nutrients?.proteins100g?.roundToInt() ?: 0
    val carbs = nutrients?.carbohydrates100g?.roundToInt() ?: 0
    val fat = nutrients?.fat100g?.roundToInt() ?: 0

    KalkyCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(0.dp),
        color = AppTheme.colors.surfaceSecondary
    ) {
        Box {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Fallback thumbnail with gray icon (no image for API results)
                Box(
                    modifier = Modifier
                        .width(dims.thumbnailSize)
                        .height(dims.thumbnailSize)
                        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                        .background(AppTheme.colors.border),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RestaurantMenu,
                        contentDescription = null,
                        modifier = Modifier.size(dims.iconCircleSize),
                        tint = AppTheme.colors.onBackgroundSecondary
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = name,
                        fontSize = dims.fontBody,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppTheme.colors.onBackground,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(dims.halfSpacing))
                    CaloriesRow(calories = calories)
                    Spacer(modifier = Modifier.height(dims.halfSpacing))
                    NutrientsRow(
                        protein = protein,
                        carbs = carbs,
                        fat = fat
                    )
                    Text(
                        text = s.customFood.per100g,
                        fontSize = dims.fontCaption,
                        color = AppTheme.colors.onBackgroundSecondary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortionPickerSheet(
    product: OpenFoodFactsProduct,
    portionGrams: Int,
    onPortionChanged: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalStrings.current
    val nutrients = product.nutriments
    val factor = portionGrams / 100.0
    val calories = ((nutrients?.energyKcal100g ?: 0.0) * factor).roundToInt()
    val protein = ((nutrients?.proteins100g ?: 0.0) * factor).roundToInt()
    val carbs = ((nutrients?.carbohydrates100g ?: 0.0) * factor).roundToInt()
    val fat = ((nutrients?.fat100g ?: 0.0) * factor).roundToInt()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = AppTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = product.productName ?: s.common.unknownProduct,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.onBackground
            )

            // Portion input
            Text(
                text = s.customFood.portionSize,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.onBackgroundSecondary
            )
            OutlinedTextField(
                value = portionGrams.toString(),
                onValueChange = { text ->
                    val grams = text.filter { it.isDigit() }.toIntOrNull() ?: 0
                    onPortionChanged(grams)
                },
                suffix = { Text(s.customFood.grams, color = AppTheme.colors.onBackgroundSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppTheme.colors.onBackgroundSecondary,
                    unfocusedBorderColor = AppTheme.colors.border,
                    focusedContainerColor = AppTheme.colors.surface,
                    unfocusedContainerColor = AppTheme.colors.surface,
                    focusedTextColor = AppTheme.colors.onBackground,
                    unfocusedTextColor = AppTheme.colors.onBackground
                )
            )

            // Quick portion buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PORTION_PRESETS.forEach { grams ->
                    KalkyCard(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onPortionChanged(grams) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (portionGrams == grams) AppTheme.colors.onBackground else AppTheme.colors.surface
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${grams}g",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (portionGrams == grams) AppTheme.colors.background else AppTheme.colors.onBackground
                            )
                        }
                    }
                }
            }

            // Calories card
            val dims = LocalDimensions.current
            KalkyCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = AppTheme.colors.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dims.rowHeight)
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(dims.iconCircleSize)
                            .background(
                                color = Color.Black,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(dims.iconSize)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = s.common.calories,
                        color = AppTheme.colors.onBackground,
                        fontSize = dims.fontBody,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = calories.toString(),
                        color = AppTheme.colors.onBackground,
                        fontSize = dims.fontBody,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "kcal",
                        color = AppTheme.colors.onBackgroundSecondary,
                        fontSize = dims.fontBody,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(26.dp))
                }
            }

            // Macros card with icons
            KalkyCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = AppTheme.colors.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    NutrientEditRow(
                        label = s.common.protein,
                        value = protein,
                        valueUnit = "g",
                        icon = Icons.Default.Restaurant,
                        activeColor = MacroColors.protein,
                        onClick = {},
                        enabled = false
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(start = 56.dp, end = 14.dp)
                            .background(AppTheme.colors.border)
                    )
                    NutrientEditRow(
                        label = s.common.carbs,
                        value = carbs,
                        valueUnit = "g",
                        icon = Icons.Default.Spa,
                        activeColor = MacroColors.carbs,
                        onClick = {},
                        enabled = false
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(start = 56.dp, end = 14.dp)
                            .background(AppTheme.colors.border)
                    )
                    NutrientEditRow(
                        label = s.common.fat,
                        value = fat,
                        valueUnit = "g",
                        icon = Icons.Default.Eco,
                        activeColor = MacroColors.fat,
                        onClick = {},
                        enabled = false
                    )
                }
            }

            KalkyButton(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = s.common.add,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    val dims = LocalDimensions.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            fontSize = dims.fontBody,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colors.onBackgroundSecondary
        )
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
