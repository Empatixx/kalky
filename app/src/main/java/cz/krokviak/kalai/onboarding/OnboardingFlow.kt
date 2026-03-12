package cz.krokviak.kalai.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.settings.SettingsUiState
import cz.krokviak.kalai.settings.components.IosInlineValuePicker
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.ui.components.KalaiButton
import cz.krokviak.kalai.ui.components.KalaiCard
import kotlinx.coroutines.launch
import java.util.Locale

private enum class OnboardingStep(val title: String) {
    GENDER("Vyber pohlaví"),
    WEIGHT("Kolik vážíš?"),
    HEIGHT("Jak jsi vysoký/á?"),
    AGE("Kolik je ti let?"),
    ACTIVITY("Jak aktivní jsi?"),
    GOAL("Jaký máš cíl?")
}

enum class GoalChoice(val label: String) {
    LOSE("Chci zhubnout"),
    MAINTAIN("Chci udržet"),
    GAIN("Chci nabrat")
}

data class OnboardingResult(
    val gender: String,
    val weight: String,
    val height: String,
    val age: String,
    val activityLevel: Int,
    val goal: GoalChoice
)

@Composable
fun OnboardingFlow(
    initialSettings: SettingsUiState,
    onFinish: (OnboardingResult) -> Unit
) {
    val steps = OnboardingStep.entries
    val pagerState = rememberPagerState(pageCount = { steps.size })
    val scope = rememberCoroutineScope()

    val weightValues = remember {
        (300..2500).map { index -> String.format(Locale.US, "%.1f", index / 10f) }
    }
    val heightValues = remember { (100..250).map { it.toString() } }
    val ageValues = remember { (1..120).map { it.toString() } }

    var gender by rememberSaveable { mutableStateOf(initialSettings.gender.ifBlank { "Muž" }) }
    var activityLevel by rememberSaveable { mutableIntStateOf(initialSettings.activityLevel.coerceIn(1, 4)) }
    var goalChoice by rememberSaveable { mutableStateOf(GoalChoice.MAINTAIN) }

    var weightIndex by rememberSaveable { mutableIntStateOf(resolveWeightIndex(initialSettings.weight)) }
    var heightIndex by rememberSaveable { mutableIntStateOf(resolveIndex(initialSettings.height, 100, 250)) }
    var ageIndex by rememberSaveable { mutableIntStateOf(resolveIndex(initialSettings.age, 1, 120)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val progress = (pagerState.currentPage + 1).toFloat() / steps.size.toFloat()
        TopBar(
            progress = progress,
            page = pagerState.currentPage,
            totalPages = steps.size,
            onBack = {
                if (pagerState.currentPage > 0) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                }
            }
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (steps[page]) {
                OnboardingStep.GENDER -> {
                    ChoiceStep(
                        title = OnboardingStep.GENDER.title,
                        options = listOf("Muž", "Žena"),
                        selectedIndex = listOf("Muž", "Žena").indexOf(gender).coerceAtLeast(0),
                        onSelected = { gender = if (it == 0) "Muž" else "Žena" }
                    )
                }

                OnboardingStep.WEIGHT -> {
                    PickerStep(
                        title = OnboardingStep.WEIGHT.title,
                        values = weightValues,
                        selectedIndex = weightIndex,
                        unitSuffix = "kg",
                        onIndexChanged = { weightIndex = it }
                    )
                }

                OnboardingStep.HEIGHT -> {
                    PickerStep(
                        title = OnboardingStep.HEIGHT.title,
                        values = heightValues,
                        selectedIndex = heightIndex,
                        unitSuffix = "cm",
                        onIndexChanged = { heightIndex = it }
                    )
                }

                OnboardingStep.AGE -> {
                    PickerStep(
                        title = OnboardingStep.AGE.title,
                        values = ageValues,
                        selectedIndex = ageIndex,
                        unitSuffix = "let",
                        onIndexChanged = { ageIndex = it }
                    )
                }

                OnboardingStep.ACTIVITY -> {
                    ChoiceStep(
                        title = OnboardingStep.ACTIVITY.title,
                        options = listOf("Sedavý", "Mírný", "Aktivní", "Velmi aktivní"),
                        selectedIndex = (activityLevel - 1).coerceIn(0, 3),
                        onSelected = { activityLevel = it + 1 }
                    )
                }

                OnboardingStep.GOAL -> {
                    val goals = GoalChoice.entries
                    ChoiceStep(
                        title = OnboardingStep.GOAL.title,
                        options = goals.map { it.label },
                        selectedIndex = goals.indexOf(goalChoice).coerceAtLeast(0),
                        onSelected = { goalChoice = goals[it] }
                    )
                }
            }
        }

        val isLastStep = pagerState.currentPage == steps.lastIndex
        KalaiButton(
            onClick = {
                if (isLastStep) {
                    onFinish(
                        OnboardingResult(
                            gender = gender,
                            weight = weightValues[weightIndex],
                            height = heightValues[heightIndex],
                            age = ageValues[ageIndex],
                            activityLevel = activityLevel,
                            goal = goalChoice
                        )
                    )
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Black,
            contentColor = Color.White
        ) {
            Text(
                text = if (isLastStep) "Dokončit" else "Pokračovat",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun TopBar(
    progress: Float,
    page: Int,
    totalPages: Int,
    onBack: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Zpět",
                tint = if (page > 0) AppTheme.colors.onBackground else AppTheme.colors.onBackgroundSecondary.copy(alpha = 0.35f),
                modifier = Modifier.clickable(enabled = page > 0, onClick = onBack)
            )
            Text(
                text = "${page + 1}/$totalPages",
                color = AppTheme.colors.onBackgroundSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = Color.Black,
            trackColor = AppTheme.colors.surfaceSecondary
        )
    }
}

@Composable
private fun PickerStep(
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
private fun ChoiceStep(
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

private fun resolveWeightIndex(value: String): Int {
    val parsed = value.toFloatOrNull() ?: return 509 // 80.9
    return ((parsed * 10f).toInt() - 300).coerceIn(0, 2200)
}

private fun resolveIndex(value: String, minValue: Int, maxValue: Int): Int {
    val parsed = value.toIntOrNull() ?: return (maxValue - minValue) / 2
    return (parsed - minValue).coerceIn(0, maxValue - minValue)
}
