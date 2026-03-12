package cz.krokviak.kalai.onboarding.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.onboarding.GoalChoice
import cz.krokviak.kalai.settings.components.IosInlineValuePicker
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.ui.components.KalaiCard

@Composable
fun GenderOnboardingPage(
    selectedGender: String,
    onSelected: (String) -> Unit
) {
    ChoiceOnboardingPage(
        title = "Vyber pohlaví",
        options = listOf("Muž", "Žena"),
        selectedIndex = listOf("Muž", "Žena").indexOf(selectedGender).coerceAtLeast(0),
        onSelected = { onSelected(if (it == 0) "Muž" else "Žena") }
    )
}

@Composable
fun WeightOnboardingPage(
    values: List<String>,
    selectedIndex: Int,
    onIndexChanged: (Int) -> Unit
) {
    PickerOnboardingPage(
        title = "Kolik vážíš?",
        values = values,
        selectedIndex = selectedIndex,
        unitSuffix = "kg",
        onIndexChanged = onIndexChanged
    )
}

@Composable
fun HeightOnboardingPage(
    values: List<String>,
    selectedIndex: Int,
    onIndexChanged: (Int) -> Unit
) {
    PickerOnboardingPage(
        title = "Jak jsi vysoký/á?",
        values = values,
        selectedIndex = selectedIndex,
        unitSuffix = "cm",
        onIndexChanged = onIndexChanged
    )
}

@Composable
fun AgeOnboardingPage(
    values: List<String>,
    selectedIndex: Int,
    onIndexChanged: (Int) -> Unit
) {
    PickerOnboardingPage(
        title = "Kolik je ti let?",
        values = values,
        selectedIndex = selectedIndex,
        unitSuffix = "let",
        onIndexChanged = onIndexChanged
    )
}

@Composable
fun ActivityOnboardingPage(
    selectedActivityLevel: Int,
    onSelected: (Int) -> Unit
) {
    ChoiceOnboardingPage(
        title = "Jak aktivní jsi?",
        options = listOf("Sedavý", "Mírný", "Aktivní", "Velmi aktivní"),
        selectedIndex = (selectedActivityLevel - 1).coerceIn(0, 3),
        onSelected = { onSelected(it + 1) }
    )
}

@Composable
fun GoalOnboardingPage(
    selectedGoal: GoalChoice,
    onSelected: (GoalChoice) -> Unit
) {
    val goals = GoalChoice.entries
    ChoiceOnboardingPage(
        title = "Jaký máš cíl?",
        options = goals.map { it.label },
        selectedIndex = goals.indexOf(selectedGoal).coerceAtLeast(0),
        onSelected = { onSelected(goals[it]) }
    )
}

@Composable
private fun PickerOnboardingPage(
    title: String,
    values: List<String>,
    selectedIndex: Int,
    unitSuffix: String,
    onIndexChanged: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            color = AppTheme.colors.onBackground,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )
        IosInlineValuePicker(
            values = values,
            selectedIndex = selectedIndex,
            onIndexChanged = onIndexChanged,
            unitSuffix = unitSuffix
        )
    }
}

@Composable
private fun ChoiceOnboardingPage(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            color = AppTheme.colors.onBackground,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )
        KalaiCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = AppTheme.colors.surface
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                options.forEachIndexed { index, label ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clickable { onSelected(index) }
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            color = AppTheme.colors.onBackground,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        if (selectedIndex == index) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    }
                    if (index < options.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(horizontal = 14.dp)
                                .background(AppTheme.colors.border)
                        )
                    }
                }
            }
        }
    }
}
