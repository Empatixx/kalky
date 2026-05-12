package cz.krokviak.kalky.scenes.customfood

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import cz.krokviak.kalky.core.theme.MacroColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.scenes.barcode.data.OpenFoodFactsProduct
import cz.krokviak.kalky.scenes.nutrientedit.MacroField
import cz.krokviak.kalky.core.ui.components.MacroPickerRow
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.core.ui.LocalDimensions
import cz.krokviak.kalky.core.ui.components.KalkyButton
import cz.krokviak.kalky.core.ui.components.KalkyCard
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualFoodEntryScene(
    viewModel: ManualFoodEntryViewModel,
    onBackClick: () -> Unit,
    onFoodAdded: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val s = LocalStrings.current

    LaunchedEffect(viewModel, onFoodAdded) {
        viewModel.foodAdded.collect { onFoodAdded() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = s.customFood.manualEntryTitle,
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
                .padding(horizontal = dims.screenPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dims.itemSpacing)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                placeholder = {
                    Text(
                        text = s.customFood.foodName,
                        color = AppTheme.colors.onBackgroundSecondary
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

            val ingredientsMode = state.sourceFoods.isNotEmpty()
            var ingredientQuery by remember { mutableStateOf("") }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = s.customFood.ingredients,
                    fontSize = dims.fontSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.onBackgroundSecondary
                )

                state.sourceFoods.forEach { food ->
                    val grams = state.sourcePortionGrams[food.id] ?: 100
                    IngredientRow(
                        name = food.name.ifBlank { s.common.unknownProduct },
                        grams = grams,
                        scaledCalories = scaledForGrams(food.calories, grams),
                        scaledProtein = scaledForGrams(food.protein, grams),
                        scaledCarbs = scaledForGrams(food.carbs, grams),
                        scaledFat = scaledForGrams(food.fat, grams),
                        gramsSuffix = s.customFood.grams,
                        kcalLabel = s.common.kcal,
                        onGramsChange = { viewModel.updateSourcePortion(food.id, it) },
                        onRemove = { viewModel.removeSourceFood(food.id) },
                    )
                }

                OutlinedTextField(
                    value = ingredientQuery,
                    onValueChange = { q ->
                        ingredientQuery = q
                        viewModel.searchIngredients(q)
                    },
                    placeholder = {
                        Text(
                            text = s.customFood.addIngredient,
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

                if (ingredientQuery.isNotBlank()) {
                    val clearQuery = {
                        ingredientQuery = ""
                        viewModel.searchIngredients("")
                    }
                    state.ingredientResults.forEach { result ->
                        IngredientResultRow(
                            name = result.name.ifBlank { s.common.unknownProduct },
                            subtitle = "${result.calories} ${s.common.kcal} · ${s.customFood.per100g}",
                            onAdd = { viewModel.addSourceFood(result); clearQuery() }
                        )
                    }
                    state.ingredientApiResults.forEach { product ->
                        val n = product.nutriments
                        IngredientResultRow(
                            name = product.productName?.takeIf { it.isNotBlank() } ?: s.common.unknownProduct,
                            subtitle = "${n?.energyKcal100g?.roundToInt() ?: 0} ${s.common.kcal} · ${s.customFood.per100g}",
                            onAdd = { viewModel.addSourceFoodFromApi(product); clearQuery() }
                        )
                    }
                }
            }

            // Calories summary card (read-only, auto-calculated)
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
                            contentDescription = s.common.calories,
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
                        text = state.calories.toString(),
                        color = AppTheme.colors.onBackground,
                        fontSize = dims.fontBody,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = s.common.kcal,
                        color = AppTheme.colors.onBackgroundSecondary,
                        fontSize = dims.fontBody,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(26.dp))
                }
            }

            // Macro editors
            KalkyCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
                color = AppTheme.colors.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MacroPickerRow(
                        label = s.common.protein,
                        value = state.protein,
                        icon = Icons.Default.Restaurant,
                        activeColor = MacroColors.protein,
                        expanded = !ingredientsMode && state.activeField == MacroField.PROTEIN,
                        onClick = { if (!ingredientsMode) viewModel.toggleField(MacroField.PROTEIN) },
                        onValueChange = viewModel::onProteinChange,
                        pickerItemHeight = 28.dp,
                        pickerVisibleItemsCount = 3,
                        pickerTextSize = 17.sp,
                        pickerHorizontalPadding = 8.dp,
                        pickerBottomPadding = 2.dp
                    )
                    MacroDivider()
                    MacroPickerRow(
                        label = s.common.carbs,
                        value = state.carbs,
                        icon = Icons.Default.Spa,
                        activeColor = MacroColors.carbs,
                        expanded = !ingredientsMode && state.activeField == MacroField.CARBS,
                        onClick = { if (!ingredientsMode) viewModel.toggleField(MacroField.CARBS) },
                        onValueChange = viewModel::onCarbsChange,
                        pickerItemHeight = 28.dp,
                        pickerVisibleItemsCount = 3,
                        pickerTextSize = 17.sp,
                        pickerHorizontalPadding = 8.dp,
                        pickerBottomPadding = 2.dp
                    )
                    MacroDivider()
                    MacroPickerRow(
                        label = s.common.fat,
                        value = state.fat,
                        icon = Icons.Default.Eco,
                        activeColor = MacroColors.fat,
                        expanded = !ingredientsMode && state.activeField == MacroField.FAT,
                        onClick = { if (!ingredientsMode) viewModel.toggleField(MacroField.FAT) },
                        onValueChange = viewModel::onFatChange,
                        pickerItemHeight = 28.dp,
                        pickerVisibleItemsCount = 3,
                        pickerTextSize = 17.sp,
                        pickerHorizontalPadding = 8.dp,
                        pickerBottomPadding = 2.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            KalkyButton(
                onClick = { viewModel.submit() },
                modifier = Modifier.fillMaxWidth(),
                containerColor = if (state.name.isBlank()) Color.Gray else Color.Black
            ) {
                Text(
                    text = s.customFood.save,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MacroDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(start = 56.dp, end = 14.dp)
            .background(AppTheme.colors.border)
    )
}

internal fun scaledForGrams(per100: Int, grams: Int): Int =
    ((per100 * grams) / 100.0).roundToInt()

@Composable
private fun IngredientRow(
    name: String,
    grams: Int,
    scaledCalories: Int,
    scaledProtein: Int,
    scaledCarbs: Int,
    scaledFat: Int,
    gramsSuffix: String,
    kcalLabel: String,
    onGramsChange: (Int) -> Unit,
    onRemove: () -> Unit,
) {
    val dims = LocalDimensions.current
    KalkyCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dims.cardCornerRadius),
        color = AppTheme.colors.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = dims.fontBody,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.onBackground,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$scaledCalories $kcalLabel · B $scaledProtein · S $scaledCarbs · T $scaledFat",
                    fontSize = dims.fontCaption,
                    color = AppTheme.colors.onBackgroundSecondary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = grams.toString(),
                onValueChange = { onGramsChange(it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0) },
                suffix = { Text(gramsSuffix, fontSize = dims.fontCaption) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.width(96.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppTheme.colors.onBackgroundSecondary,
                    unfocusedBorderColor = AppTheme.colors.border,
                    focusedTextColor = AppTheme.colors.onBackground,
                    unfocusedTextColor = AppTheme.colors.onBackground
                )
            )
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = LocalStrings.current.common.close,
                    tint = AppTheme.colors.onBackgroundSecondary
                )
            }
        }
    }
}

@Composable
private fun IngredientResultRow(
    name: String,
    subtitle: String,
    onAdd: () -> Unit,
) {
    val dims = LocalDimensions.current
    KalkyCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAdd),
        shape = RoundedCornerShape(dims.cardCornerRadius),
        color = AppTheme.colors.surfaceSecondary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = dims.fontBody,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.onBackground,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = dims.fontCaption,
                    color = AppTheme.colors.onBackgroundSecondary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(color = AppTheme.colors.onBackground, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = LocalStrings.current.common.add,
                    tint = AppTheme.colors.background,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
