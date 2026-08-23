package cz.krokviak.kalky.scenes.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import cz.krokviak.kalky.core.theme.MacroColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import cz.krokviak.kalky.core.common.entities.FoodItemEntity
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.core.ui.LocalDimensions
import cz.krokviak.kalky.core.ui.components.KalkyCard
import cz.krokviak.kalky.core.common.formatTime

@Composable
fun FoodItemCard(
    foodItem: FoodItemEntity,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    if (foodItem.loading) {
        FoodItemLoadingCard(foodItem)
    } else {
        FoodItemLoadedCard(
            foodItem,
            isSelected = isSelected,
            onClick = onClick,
            onLongClick = onLongClick
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FoodItemLoadedCard(
    foodItem: FoodItemEntity,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val s = LocalStrings.current
    val cornerShape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
    val cardModifier = if (isSelected) {
        Modifier
            .fillMaxWidth()
            .clip(cornerShape)
            .border(width = 2.dp, color = AppTheme.colors.onBackground, shape = cornerShape)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    } else {
        Modifier
            .fillMaxWidth()
            .clip(cornerShape)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    }
    KalkyCard(
        modifier = cardModifier,
        shape = cornerShape,
        contentPadding = PaddingValues(0.dp),
        color = AppTheme.colors.surfaceSecondary
    ) {
        Box {
            Row(modifier = Modifier.fillMaxWidth()) {
                FoodItemImage(foodItem = foodItem)
                FoodItemInfo(foodItem = foodItem)
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
                        contentDescription = s.home.selected,
                        tint = AppTheme.colors.background,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FoodItemImage(
    foodItem: FoodItemEntity,
    showBadge: Boolean = true,
    fallbackTint: Color = AppTheme.colors.onBackgroundSecondary
) {
    val dims = LocalDimensions.current
    val s = LocalStrings.current
    val badgeTime = remember(foodItem.createdAt) {
        foodItem.createdAt.formatTime()
    }
    Box(
        modifier = Modifier
            .width(dims.thumbnailSize)
            .height(dims.thumbnailSize)
            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
    ) {
        if (foodItem.localImagePath.isNotEmpty()) {
            AsyncImage(
                model = foodItem.localImagePath,
                contentDescription = s.common.cdFoodImage,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
                filterQuality = FilterQuality.Low
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(AppTheme.colors.border),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = null,
                    modifier = Modifier.size(dims.iconCircleSize),
                    tint = fallbackTint
                )
            }
        }
        if (showBadge) {
            Badge(timeText = badgeTime)
        }
    }
}

@Composable
fun BoxScope.Badge(timeText: String) {
    Box(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(4.dp)
            .background(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = timeText,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
    }
}

@Composable
fun RowScope.FoodItemInfo(foodItem: FoodItemEntity) {
    val dims = LocalDimensions.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .align(Alignment.CenterVertically)
    ) {
        Text(
            text = foodItem.name.ifBlank { LocalStrings.current.home.unknownFood },
            fontSize = dims.fontBody,
            fontWeight = FontWeight.ExtraBold,
            color = AppTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        CaloriesRow(calories = foodItem.calories)
        Spacer(modifier = Modifier.height(8.dp))
        NutrientsRow(
            protein = foodItem.protein,
            carbs = foodItem.carbs,
            fat = foodItem.fat
        )
    }
}

@Composable
fun CaloriesRow(calories: Int) {
    val dims = LocalDimensions.current
    val s = LocalStrings.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.LocalFireDepartment,
            contentDescription = s.common.calories,
            modifier = Modifier.size(dims.iconSize),
            tint = AppTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "$calories ${s.common.kcal}", fontSize = dims.fontBody, fontWeight = FontWeight.SemiBold, color = AppTheme.colors.onBackground)
    }
}

@Composable
fun NutrientsRow(protein: Int, carbs: Int, fat: Int) {
    val s = LocalStrings.current
    // Equal thirds: fixed gaps let three two-digit values overflow, which wrapped
    // the last one onto a second line and made the card taller than its siblings.
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NutrientItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Restaurant,
            contentDescription = s.common.protein,
            valueText = "$protein g",
            tintColor = MacroColors.protein
        )
        NutrientItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Spa,
            contentDescription = s.common.carbs,
            valueText = "$carbs g",
            tintColor = MacroColors.carbs
        )
        NutrientItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Eco,
            contentDescription = s.common.fat,
            valueText = "$fat g",
            tintColor = MacroColors.fat
        )
    }
}

@Composable
fun NutrientItem(
    icon: ImageVector,
    contentDescription: String,
    valueText: String,
    tintColor: Color,
    modifier: Modifier = Modifier
) {
    val dims = LocalDimensions.current
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(dims.iconSize * 0.83f),
            tint = tintColor
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = valueText,
            fontSize = dims.fontSmall,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colors.onBackground,
            maxLines = 1,
            softWrap = false
        )
    }
}

private const val LOADING_ANIMATION_DURATION_MS = 6000

@Composable
fun FoodItemLoadingCard(foodItem: FoodItemEntity) {
    val progress = remember(foodItem.id) { Animatable(0f) }
    LaunchedEffect(foodItem.id) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = LOADING_ANIMATION_DURATION_MS,
                easing = LinearEasing
            )
        )
    }
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val skeletonAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeletonAlpha"
    )
    KalkyCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
        contentPadding = PaddingValues(0.dp),
        color = AppTheme.colors.surfaceSecondary
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            FoodItemLoadingImage(foodItem = foodItem, progress = (progress.value * 100).toInt())
            FoodItemLoadingInfo(skeletonAlpha = skeletonAlpha)
        }
    }
}

@Composable
fun FoodItemLoadingImage(
    foodItem: FoodItemEntity,
    progress: Int
) {
    val dims = LocalDimensions.current
    val isDarkTheme = AppTheme.colors.background.luminance() < 0.5f
    val loadingOverlayAlpha = if (isDarkTheme) 0.62f else 0.48f
    val loadingProgressColor = Color.White.copy(alpha = 0.96f)
    val loadingTrackColor = Color.White.copy(alpha = 0.28f)

    Box(
        modifier = Modifier
            .width(dims.thumbnailSize)
            .height(dims.thumbnailSize)
            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
    ) {
        AsyncImage(
            model = foodItem.localImagePath,
            contentDescription = LocalStrings.current.common.cdFoodImage,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            filterQuality = FilterQuality.Low
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = loadingOverlayAlpha))
        )
        Box(
            modifier = Modifier.matchParentSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularPercentageIndicator(
                percentage = progress,
                backgroundColor = loadingTrackColor,
                progressColor = loadingProgressColor,
                modifier = Modifier.size(70.dp)
            )
        }
    }
}

@Composable
fun RowScope.FoodItemLoadingInfo(skeletonAlpha: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .align(Alignment.CenterVertically)
    ) {
        Text(
            text = LocalStrings.current.home.computingMacros,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        SkeletonPlaceholder(
            alpha = skeletonAlpha,
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SkeletonPlaceholder(
                alpha = skeletonAlpha,
                modifier = Modifier
                    .weight(1f)
                    .height(16.dp)
            )
            SkeletonPlaceholder(
                alpha = skeletonAlpha,
                modifier = Modifier
                    .weight(1f)
                    .height(16.dp)
            )
            SkeletonPlaceholder(
                alpha = skeletonAlpha,
                modifier = Modifier
                    .weight(1f)
                    .height(16.dp)
            )
        }
    }
}

private val SkeletonBaseColor = Color.Gray

@Composable
fun SkeletonPlaceholder(
    alpha: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(
            SkeletonBaseColor.copy(alpha = alpha),
            shape = RoundedCornerShape(4.dp)
        )
    )
}
