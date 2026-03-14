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
import cz.krokviak.kalai.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalai.common.entities.FoodItemEntity
import cz.krokviak.kalai.home.components.CaloriesRow
import cz.krokviak.kalai.home.components.FoodItemImage
import cz.krokviak.kalai.home.components.NutrientsRow
import cz.krokviak.kalai.i18n.LocalStrings
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.ui.components.KalaiButton
import cz.krokviak.kalai.ui.components.KalaiCard
import cz.krokviak.kalai.ui.components.KalaiSegmentedControl
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
    var selectedTab by remember { mutableStateOf(0) } // 0=All, 1=My Foods, 2=Recently Used

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

                KalaiSegmentedControl(
                    selectedIndex = selectedTab,
                    items = listOf(s.customFood.all, s.customFood.myFoods, s.customFood.history),
                    onItemSelected = { selectedTab = it },
                    modifier = Modifier.fillMaxWidth(),
                    textSize = 13.sp
                )

                val hasAnyResults = when (selectedTab) {
                    1 -> uiState.customFoods.isNotEmpty()
                    2 -> uiState.historyItems.isNotEmpty()
                    else -> uiState.customFoods.isNotEmpty() || uiState.historyItems.isNotEmpty() || uiState.apiResults.isNotEmpty()
                }

                if (!hasAnyResults && !uiState.isLoading) {
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
                        if (selectedTab == 1) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable(onClick = onAddNewClick)
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = AppTheme.colors.onBackgroundSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = s.customFood.addManually,
                                        fontSize = 14.sp,
                                        color = AppTheme.colors.onBackgroundSecondary
                                    )
                                }
                            }
                        }
                        if (uiState.customFoods.isNotEmpty() && selectedTab != 2) {
                            item {
                                Text(
                                    text = s.customFood.myFoods,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppTheme.colors.onBackgroundSecondary
                                )
                            }
                            items(uiState.customFoods, key = { "custom_${it.id}" }) { item ->
                                HistoryFoodItem(
                                    item = item,
                                    isSelected = item.id in uiState.selectedItems,
                                    onClick = { viewModel.toggleSelection(item.id) }
                                )
                            }
                        }
                        if (uiState.historyItems.isNotEmpty() && selectedTab != 1) {
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
                        if (uiState.apiResults.isNotEmpty() && selectedTab == 0) {
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
                                    onClick = { viewModel.selectApiProduct(product) }
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

    uiState.selectedApiProduct?.let { product ->
        PortionPickerSheet(
            product = product,
            portionGrams = uiState.portionGrams,
            onPortionChanged = viewModel::setPortionGrams,
            onConfirm = viewModel::confirmAddApiProduct,
            onDismiss = viewModel::dismissPortionPicker
        )
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
                listOf(50, 100, 150, 200, 250).forEach { grams ->
                    KalaiCard(
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

            // Scaled macros display
            KalaiCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = AppTheme.colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = s.common.calories,
                            fontSize = 16.sp,
                            color = AppTheme.colors.onBackground
                        )
                        Text(
                            text = "$calories kcal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.onBackground
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = s.common.protein, fontSize = 14.sp, color = AppTheme.colors.onBackgroundSecondary)
                        Text(text = "${protein}g", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AppTheme.colors.onBackground)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = s.common.carbs, fontSize = 14.sp, color = AppTheme.colors.onBackgroundSecondary)
                        Text(text = "${carbs}g", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AppTheme.colors.onBackground)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = s.common.fat, fontSize = 14.sp, color = AppTheme.colors.onBackgroundSecondary)
                        Text(text = "${fat}g", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AppTheme.colors.onBackground)
                    }
                }
            }

            KalaiButton(
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
