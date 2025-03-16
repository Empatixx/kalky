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
import androidx.compose.ui.unit.dp
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                modifier = Modifier
                    .size(350.dp))
        }
        NutrientEditRow(
            value = uiState.calories,
            modifier = Modifier.fillMaxWidth(),
            title = "Kalorie",
            onValueChange = { nutrientEditViewModel.onCalorieChange(it) },
            icon = Icons.Filled.LocalFireDepartment,
            activeColor = colorResource(id = R.color.black)
        )
        NutrientEditRow(
            value = uiState.protein,
            modifier = Modifier.fillMaxWidth(),
            title = "Bílkoviny",
            onValueChange = { nutrientEditViewModel.onProteinChange(it) },
            icon = ImageVector.vectorResource(R.drawable.chicken_leg),
            activeColor = colorResource(id = R.color.proteinColor)
        )
        NutrientEditRow(
            value = uiState.carbs,
            modifier = Modifier.fillMaxWidth(),
            title = "Sacharidy",
            onValueChange = { nutrientEditViewModel.onCarbsChange(it) },
            icon = ImageVector.vectorResource(R.drawable.wheat),
            activeColor = colorResource(id = R.color.carbsColor)
        )
        NutrientEditRow(
            value = uiState.fat,
            modifier = Modifier.fillMaxWidth(),
            title = "Tuky",
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
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
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
        Box(
            contentAlignment = Alignment.Center
        ) {
            CupertinoText(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

