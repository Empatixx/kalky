package cz.krokviak.kalai.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import cz.krokviak.kalai.R
import cz.krokviak.kalai.camera.entities.FoodItemEntity

@Composable
fun FoodItemCard(foodItem: FoodItemEntity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image on the left, flush to the card edges
            Image(
                painter = rememberAsyncImagePainter(model = foodItem.localImagePath),
                contentDescription = "Food image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(150.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )

            // Text info on the right
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .align(Alignment.CenterVertically),
            ) {
                // Food name
                Text(
                    text = foodItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // First row: Icon + Kcal
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Calories",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${foodItem.calories} kcal")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Second row: Protein, Carbs, Fat side by side
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Protein
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.meat_svgrepo_com),
                            contentDescription = "Protein",
                            modifier = Modifier.size(24.dp),
                            tint = colorResource(id = R.color.proteinColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${foodItem.protein}g")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Carbs
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.wheat),
                            contentDescription = "Carbs",
                            modifier = Modifier.size(24.dp),
                            tint = colorResource(id = R.color.carbsColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${foodItem.carbs}g")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Fat
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.avocado),
                            contentDescription = "Fat",
                            modifier = Modifier.size(24.dp),
                            tint = colorResource(id = R.color.fatColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${foodItem.fat}g")
                    }
                }
            }
        }
    }
}
