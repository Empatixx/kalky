package cz.krokviak.kalky.home.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import cz.krokviak.kalky.theme.MacroColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import cz.krokviak.kalky.common.entities.FoodItemEntity
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.ui.LocalDimensions
import cz.krokviak.kalky.ui.components.KalkyCard
import cz.krokviak.kalky.common.formatTime

@Composable
fun FoodItemCard(
    foodItem: FoodItemEntity,
    progress: Int,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    if (foodItem.loading) {
        FoodItemLoadingCard(foodItem, progress)
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
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(16.dp),
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
fun FoodItemImage(foodItem: FoodItemEntity, showBadge: Boolean = true) {
    val dims = LocalDimensions.current
    // Compute the badge time only when foodItem.createdAt changes
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
                contentDescription = "Food image",
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
                    tint = AppTheme.colors.onBackgroundSecondary
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
            text = foodItem.name ?: "Neznámé jídlo",
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
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.LocalFireDepartment,
            contentDescription = "Calories",
            modifier = Modifier.size(dims.iconSize),
            tint = AppTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "$calories kcal", fontSize = dims.fontBody, fontWeight = FontWeight.SemiBold, color = AppTheme.colors.onBackground)
    }
}

@Composable
fun NutrientsRow(protein: Int, carbs: Int, fat: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        NutrientItem(
            icon = Icons.Default.Restaurant,
            contentDescription = "Protein",
            valueText = "$protein g",
            tintColor = MacroColors.protein
        )
        Spacer(modifier = Modifier.width(16.dp))
        NutrientItem(
            icon = Icons.Default.Spa,
            contentDescription = "Carbs",
            valueText = "$carbs g",
            tintColor = MacroColors.carbs
        )
        Spacer(modifier = Modifier.width(16.dp))
        NutrientItem(
            icon = Icons.Default.Eco,
            contentDescription = "Fat",
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
    tintColor: Color
) {
    val dims = LocalDimensions.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(dims.iconSize * 0.83f),
            tint = tintColor
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = valueText, fontSize = dims.fontSmall, fontWeight = FontWeight.SemiBold, color = AppTheme.colors.onBackground)
    }
}


@Composable
fun FoodItemLoadingCard(
    foodItem: FoodItemEntity,
    progress: Int
) {
    KalkyCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(0.dp),
        color = AppTheme.colors.surfaceSecondary
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            FoodItemLoadingImage(foodItem = foodItem, progress = progress)
            FoodItemLoadingInfo()
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
        // The underlying food image
        AsyncImage(
            model = foodItem.localImagePath,
            contentDescription = "Food image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            filterQuality = FilterQuality.Low
        )
        // Dark overlay to indicate loading
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = loadingOverlayAlpha))
        )
        // Centered progress indicator
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
fun RowScope.FoodItemLoadingInfo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .align(Alignment.CenterVertically)
    ) {
        Text(
            text = "Počítám makroživiny...",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Skeleton placeholder for the calorie row
        SkeletonPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Skeleton placeholders for the Protein/Carbs/Fat row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SkeletonPlaceholder(
                modifier = Modifier
                    .weight(1f)
                    .height(16.dp)
            )
            SkeletonPlaceholder(
                modifier = Modifier
                    .weight(1f)
                    .height(16.dp)
            )
            SkeletonPlaceholder(
                modifier = Modifier
                    .weight(1f)
                    .height(16.dp)
            )
        }
    }
}

@Composable
fun SkeletonPlaceholder(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedColor by infiniteTransition.animateColor(
        initialValue = Color.Gray.copy(alpha = 0.3f),
        targetValue = Color.Gray.copy(alpha = 0.7f),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier.background(
            animatedColor,
            shape = RoundedCornerShape(4.dp)
        )
    )
}
