package cz.krokviak.kalai.camera.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import cz.krokviak.kalai.R

@Composable
fun NutrientsGrid(
    caloriesValue: String,
    carbsValue: String,
    proteinValue: String,
    fatsValue: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PhotoNutrientCard(
                "Calories",
                caloriesValue,
                icon = Icons.Outlined.LocalFireDepartment,
                modifier = Modifier.weight(1f)
            )
            PhotoNutrientCard(
                "Carbs", carbsValue,
                icon = ImageVector.vectorResource(R.drawable.wheat),
                iconTintColor = colorResource(id = R.color.carbsColor),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PhotoNutrientCard(
                "Protein",
                proteinValue,
                icon = ImageVector.vectorResource(R.drawable.meat_svgrepo_com),
                iconTintColor = colorResource(id = R.color.proteinColor),
                modifier = Modifier.weight(1f)
            )
            PhotoNutrientCard(
                "Fats", fatsValue,
                icon = ImageVector.vectorResource(R.drawable.avocado),
                iconTintColor = colorResource(id = R.color.fatColor),
                modifier = Modifier.weight(1f)
            )
        }
    }
}