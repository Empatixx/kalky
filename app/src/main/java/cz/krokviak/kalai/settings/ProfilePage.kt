package cz.krokviak.kalai.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cz.krokviak.kalai.common.NutrientEditRoute
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.ui.components.KalaiButton
import cz.krokviak.kalai.ui.components.KalaiCard
import cz.krokviak.kalai.ui.components.KalaiSegmentedControl

@Composable
fun ProfilePage(
    uiState: SettingsUiState,
    viewModel: SettingsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val genderOptions = listOf("Muž", "Žena")
    val activityLabels = listOf("Sedavý", "Mírný", "Aktivní", "Velmi")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Profil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = AppTheme.colors.onBackground
        )

        // Personal info section
        SectionHeader("Osobní údaje")

        ProfileTextField(
            label = "Váha",
            value = uiState.weight,
            unit = "kg",
            onValueChange = viewModel::onWeightChange
        )

        ProfileTextField(
            label = "Výška",
            value = uiState.height,
            unit = "cm",
            onValueChange = viewModel::onHeightChange
        )

        ProfileTextField(
            label = "Věk",
            value = uiState.age,
            unit = "let",
            onValueChange = viewModel::onAgeChange,
            keyboardType = KeyboardType.Number
        )

        // BMI display
        uiState.bmi?.let { bmi ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppTheme.colors.surfaceSecondary)
                    .border(1.dp, AppTheme.colors.border, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "BMI",
                    color = AppTheme.colors.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "%.1f".format(bmi),
                    color = AppTheme.colors.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Gender selector
        Text(
            text = "Pohlaví",
            color = AppTheme.colors.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        KalaiSegmentedControl(
            selectedIndex = genderOptions.indexOf(uiState.gender).coerceAtLeast(0),
            items = genderOptions,
            onItemSelected = { viewModel.onGenderChange(genderOptions[it]) },
            modifier = Modifier.fillMaxWidth()
        )

        // Activity level
        Text(
            text = "Úroveň aktivity",
            color = AppTheme.colors.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        KalaiSegmentedControl(
            selectedIndex = (uiState.activityLevel - 1).coerceIn(0, 3),
            items = activityLabels,
            onItemSelected = { viewModel.onActivityLevelChange(it + 1) },
            modifier = Modifier.fillMaxWidth()
        )

        // Save button
        KalaiButton(
            onClick = { viewModel.save() },
            modifier = Modifier.fillMaxWidth(),
            containerColor = AppTheme.colors.primary,
            contentColor = AppTheme.colors.onPrimary
        ) {
            Text(
                text = if (uiState.saved) "Uloženo" else "Uložit",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Nutrient goals navigation
        SectionHeader("Cíle výživy")

        KalaiCard(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, AppTheme.colors.border, RoundedCornerShape(16.dp)),
            contentPadding = PaddingValues(0.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(NutrientEditRoute) }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Úprava makroživin",
                    color = AppTheme.colors.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = AppTheme.colors.onBackgroundSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = AppTheme.colors.onBackgroundSecondary,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    unit: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Decimal
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, AppTheme.colors.border, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = AppTheme.colors.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            BasicTextField(
                value = value,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
                onValueChange = onValueChange,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.End,
                    color = AppTheme.colors.onBackground
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            Text(
                text = unit,
                color = AppTheme.colors.onBackgroundSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
