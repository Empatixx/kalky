package cz.krokviak.kalky.scenes.nutrientedit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.krokviak.kalky.core.theme.MacroColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalky.core.theme.AppTheme
import cz.krokviak.kalky.scenes.nutrientedit.components.VerticalCalorieCard
import cz.krokviak.kalky.core.ui.components.KalkyCard
import cz.krokviak.kalky.core.ui.components.MacroPickerRow
import cz.krokviak.kalky.core.i18n.LocalStrings
import cz.krokviak.kalky.core.ui.LocalDimensions
import cz.krokviak.kalky.core.ui.components.KalkyGradientBackground

@Composable
fun NutrientEditScene(
    onBackClick: () -> Unit,
    nutrientEditViewModel: NutrientEditViewModel,
    uiState: NutrientEditState
) {
    val dims = LocalDimensions.current
    val strings = LocalStrings.current

    KalkyGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dims.screenPadding, vertical = dims.itemSpacing),
            verticalArrangement = Arrangement.spacedBy(dims.itemSpacing)
        ) {
            NutrientEditTopBar(
                onBackClick = onBackClick,
                title = strings.nutrientEdit.title
            )
            VerticalCalorieCard(
                currentCalories = uiState.calories,
                calorieRatio = 0.5f,
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = strings.nutrientEdit.macronutrients,
                    color = AppTheme.colors.onBackgroundSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 12.dp)
                )
                KalkyCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppTheme.colors.border, RoundedCornerShape(LocalDimensions.current.cardCornerRadius)),
                    shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
                    color = AppTheme.colors.surface
                ) {
                    Column {
                        MacroPickerRow(
                            label = strings.common.protein,
                            value = uiState.protein,
                            icon = Icons.Default.Restaurant,
                            activeColor = MacroColors.protein,
                            expanded = uiState.activeField == MacroField.PROTEIN,
                            onClick = { nutrientEditViewModel.toggleField(MacroField.PROTEIN) },
                            onValueChange = nutrientEditViewModel::onProteinChange
                        )
                        GroupDivider()
                        MacroPickerRow(
                            label = strings.common.carbs,
                            value = uiState.carbs,
                            icon = Icons.Default.Spa,
                            activeColor = MacroColors.carbs,
                            expanded = uiState.activeField == MacroField.CARBS,
                            onClick = { nutrientEditViewModel.toggleField(MacroField.CARBS) },
                            onValueChange = nutrientEditViewModel::onCarbsChange
                        )
                        GroupDivider()
                        MacroPickerRow(
                            label = strings.common.fat,
                            value = uiState.fat,
                            icon = Icons.Default.Eco,
                            activeColor = MacroColors.fat,
                            expanded = uiState.activeField == MacroField.FAT,
                            onClick = { nutrientEditViewModel.toggleField(MacroField.FAT) },
                            onValueChange = nutrientEditViewModel::onFatChange
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NutrientEditTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    title: String = ""
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable(onClick = onBackClick)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = LocalStrings.current.common.cdBack,
                tint = AppTheme.colors.onBackground
            )
            Text(
                text = LocalStrings.current.common.back,
                color = AppTheme.colors.onBackground,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = title,
            color = AppTheme.colors.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 48.dp)
        )
    }
}

@Composable
private fun GroupDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(start = 56.dp, end = 14.dp)
            .background(AppTheme.colors.border)
    )
}
