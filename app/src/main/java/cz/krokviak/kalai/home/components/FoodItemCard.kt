package cz.krokviak.kalai.home.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import cz.krokviak.kalai.R
import cz.krokviak.kalai.camera.entities.FoodItemEntity
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

@Composable
fun FoodItemCard(foodItem: FoodItemEntity,
                 progress: Int) {
    val context = LocalContext.current
    if (foodItem.loading) {
        // Show the "loading" version
        FoodItemLoadingCard(foodItem, progress)
    } else {
        // Show the normal "loaded" version
        FoodItemLoadedCard(
            foodItem,
            onClick = { openFoodDetails(context, foodItem) }
        )
    }
}

fun openFoodDetails(
    context: Context,
    foodItem: FoodItemEntity
) {
    context.startActivity(Intent(context, DetailActivity::class.java))
}


@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun FoodItemLoadedCard(foodItem: FoodItemEntity,
                       onClick: () -> Unit
) {
    CupertinoSection(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(0.dp),
        color = colorResource(id = R.color.lightBlueGray)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // IMAGE
            Box(
                modifier = Modifier
                    .width(125.dp)
                    .height(125.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = foodItem.localImagePath),
                    contentDescription = "Food image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                )
            }

            // TEXT
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .align(Alignment.CenterVertically),
            ) {
                CupertinoText(
                    text = foodItem.name ?: "Neznámé jídlo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Calories row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CupertinoIcon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Calories",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    CupertinoText("${foodItem.calories} kcal")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Protein, Carbs, Fat
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Protein
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CupertinoIcon(
                            imageVector = ImageVector.vectorResource(R.drawable.chicken_leg),
                            contentDescription = "Protein",
                            modifier = Modifier.size(20.dp),
                            tint = colorResource(id = R.color.proteinColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        CupertinoText("${foodItem.protein}g")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Carbs
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CupertinoIcon(
                            imageVector = ImageVector.vectorResource(R.drawable.wheat),
                            contentDescription = "Carbs",
                            modifier = Modifier.size(20.dp),
                            tint = colorResource(id = R.color.carbsColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        CupertinoText("${foodItem.carbs}g")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Fat
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CupertinoIcon(
                            imageVector = ImageVector.vectorResource(R.drawable.avocado),
                            contentDescription = "Fat",
                            modifier = Modifier.size(20.dp),
                            tint = colorResource(id = R.color.fatColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        CupertinoText("${foodItem.fat}g")
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun FoodItemLoadingCard(foodItem: FoodItemEntity,
                        progress: Int) {
    CupertinoSection(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(0.dp),
        color = colorResource(id = R.color.lightBlueGray)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // IMAGE + LOADING OVERLAY
            Box(
                modifier = Modifier
                    .width(125.dp)
                    .height(125.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = foodItem.localImagePath),
                    contentDescription = "Food image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                // Dark overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )

                // Centered Progress Indicator
                Box(
                    modifier = Modifier
                        .matchParentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularPercentageIndicator(
                        percentage = progress,
                        backgroundColor = colorResource(id = R.color.lightBlueGray).copy(alpha = 0.5f),
                        progressColor = colorResource(id = R.color.lightBlueGray),
                        modifier = Modifier.size(70.dp) // pick a size
                    )
                }
            }

            // TEXT AREA WITH SKELETONS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .align(Alignment.CenterVertically),
            ) {
                CupertinoText(
                    text = "Počítám makroživiny...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Skeleton for the calorie row
                SkeletonPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Skeletons for Protein/Carbs/Fat row
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
    }
}


@Composable
fun SkeletonPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            )
    )
}
