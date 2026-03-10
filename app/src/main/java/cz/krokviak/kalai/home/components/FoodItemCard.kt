package cz.krokviak.kalai.home.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import cz.krokviak.kalai.R
import cz.krokviak.kalai.common.entities.FoodItemEntity
import cz.krokviak.kalai.theme.AppTheme
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.section.CupertinoSection
import cz.krokviak.kalai.common.formatTime

@Composable
fun FoodItemCard(
    foodItem: FoodItemEntity,
    progress: Int,
    onClick: () -> Unit
) {
    if (foodItem.loading) {
        // Show the "loading" version
        FoodItemLoadingCard(foodItem, progress)
    } else {
        // Show the normal "loaded" version
        FoodItemLoadedCard(
            foodItem,
            onClick = onClick
        )
    }
}

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun FoodItemLoadedCard(
    foodItem: FoodItemEntity,
    onClick: () -> Unit
) {
    CupertinoSection(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(0.dp),
        color = AppTheme.colors.surfaceSecondary
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            FoodItemImage(foodItem = foodItem)
            FoodItemInfo(foodItem = foodItem)
        }
    }
}

@Composable
fun FoodItemImage(foodItem: FoodItemEntity) {
    // Compute the badge time only when foodItem.createdAt changes
    val badgeTime = remember(foodItem.createdAt) {
        foodItem.createdAt.formatTime()
    }
    Box(
        modifier = Modifier
            .width(125.dp)
            .height(125.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(foodItem.localImagePath)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .size(200)
                .build(),
            contentDescription = "Food image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            filterQuality = FilterQuality.Low
        )
        Badge(timeText = badgeTime)
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
        CupertinoText(
            text = timeText,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
    }
}

@Composable
fun RowScope.FoodItemInfo(foodItem: FoodItemEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .align(Alignment.CenterVertically)
    ) {
        CupertinoText(
            text = foodItem.name ?: "Neznámé jídlo",
            fontSize = 18.sp,
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
    Row(verticalAlignment = Alignment.CenterVertically) {
        CupertinoIcon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = "Calories",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        CupertinoText(text = "$calories kcal", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AppTheme.colors.onBackground)
    }
}

@Composable
fun NutrientsRow(protein: Int, carbs: Int, fat: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        NutrientItem(
            icon = ImageVector.vectorResource(R.drawable.chicken_leg),
            contentDescription = "Protein",
            valueText = "$protein g",
            tintRes = R.color.proteinColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        NutrientItem(
            icon = ImageVector.vectorResource(R.drawable.wheat),
            contentDescription = "Carbs",
            valueText = "$carbs g",
            tintRes = R.color.carbsColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        NutrientItem(
            icon = ImageVector.vectorResource(R.drawable.avocado),
            contentDescription = "Fat",
            valueText = "$fat g",
            tintRes = R.color.fatColor
        )
    }
}

@Composable
fun NutrientItem(
    icon: ImageVector,
    contentDescription: String,
    valueText: String,
    tintRes: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CupertinoIcon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp),
            tint = colorResource(id = tintRes)
        )
        Spacer(modifier = Modifier.width(4.dp))
        CupertinoText(text = valueText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppTheme.colors.onBackground)
    }
}


@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun FoodItemLoadingCard(
    foodItem: FoodItemEntity,
    progress: Int
) {
    CupertinoSection(
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
    Box(
        modifier = Modifier
            .width(125.dp)
            .height(125.dp)
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
                .background(Color.Black.copy(alpha = 0.5f))
        )
        // Centered progress indicator
        Box(
            modifier = Modifier.matchParentSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularPercentageIndicator(
                percentage = progress,
                backgroundColor = AppTheme.colors.surfaceSecondary.copy(alpha = 0.5f),
                progressColor = AppTheme.colors.surfaceSecondary,
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
        CupertinoText(
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