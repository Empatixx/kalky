package cz.krokviak.kalai.nutrientedit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.R
import cz.krokviak.kalai.nutrientedit.components.MacroNutrientRatioDonutChart
import cz.krokviak.kalai.nutrientedit.components.NutrientEditRow
import io.github.alexzhirkevich.cupertino.CupertinoText


@Composable
fun NutrientEditScene(
    onBackClick: () -> Unit,
    onNutrientEdit: (Any?) -> Unit,
    nutrientEditViewModel: NutrientEditViewModel,
    uiState: NutrientEditState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NutrientEditTopBar(
            onBackClick = onBackClick
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            MacroNutrientRatioDonutChart(
                protein = uiState.protein,
                carbs = uiState.carbs,
                fat = uiState.fat,
                modifier = Modifier.fillMaxWidth()
                    .height(250.dp))
        }

        CupertinoText(
            text = "Kalorie",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        NutrientEditRow(
            value = uiState.calories,
            valueUnit = "kcal",
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { nutrientEditViewModel.onCalorieChange(it) },
            icon = Icons.Filled.LocalFireDepartment,
            activeColor = colorResource(id = R.color.black),
        )
        CupertinoText(
            text = "Bilkoviny",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        NutrientEditRow(
            value = uiState.protein,
            valueUnit = "g",
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { nutrientEditViewModel.onProteinChange(it) },
            icon = ImageVector.vectorResource(R.drawable.chicken_leg),
            activeColor = colorResource(id = R.color.proteinColor)
        )
        CupertinoText(
            text = "Sacharidy",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        NutrientEditRow(
            value = uiState.carbs,
            valueUnit = "g",
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { nutrientEditViewModel.onCarbsChange(it) },
            icon = ImageVector.vectorResource(R.drawable.wheat),
            activeColor = colorResource(id = R.color.carbsColor)
        )
        CupertinoText(
            text = "Tuky",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        NutrientEditRow(
            value = uiState.fat,
            valueUnit = "g",
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { nutrientEditViewModel.onFatChange(it) },
            icon = ImageVector.vectorResource(R.drawable.avocado),
            activeColor = colorResource(id = R.color.fatColor)
        )

    }
}

@Composable
fun NutrientEditTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    title: String = "Úprava makroživin"
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .background(
                    color = Color.Gray.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Go Back",
                tint = Color.White
            )
        }
        CupertinoText(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


