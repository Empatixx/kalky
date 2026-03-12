package cz.krokviak.kalai.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.onboarding.components.OnboardingProgressBar
import cz.krokviak.kalai.onboarding.pages.ActivityOnboardingPage
import cz.krokviak.kalai.onboarding.pages.AgeOnboardingPage
import cz.krokviak.kalai.onboarding.pages.GenderOnboardingPage
import cz.krokviak.kalai.onboarding.pages.GoalOnboardingPage
import cz.krokviak.kalai.onboarding.pages.HeightOnboardingPage
import cz.krokviak.kalai.onboarding.pages.WeightOnboardingPage
import cz.krokviak.kalai.settings.AppPreferencesManager
import cz.krokviak.kalai.settings.UnitSystem
import cz.krokviak.kalai.settings.formatHeightForDisplay
import cz.krokviak.kalai.settings.formatWeightForDisplay
import cz.krokviak.kalai.settings.heightUnitLabel
import cz.krokviak.kalai.settings.weightUnitLabel
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.ui.components.KalaiButton
import cz.krokviak.kalai.ui.components.KalaiGradientBackground

@Composable
fun OnboardingPage(
    onboardingViewModel: OnboardingViewModel,
    onFinish: (OnboardingResult) -> Unit
) {
    val uiState by onboardingViewModel.uiState.collectAsState()
    val weightValues = onboardingViewModel.weightValues
    val heightValues = onboardingViewModel.heightValues
    val ageValues = onboardingViewModel.ageValues
    val unitSystem by AppPreferencesManager.unitSystem.collectAsState()
    val displayWeightValues = remember(weightValues, unitSystem) {
        if (unitSystem == UnitSystem.METRIC) {
            weightValues
        } else {
            weightValues.map { formatWeightForDisplay(it.toFloat(), unitSystem) }
        }
    }
    val displayHeightValues = remember(heightValues, unitSystem) {
        if (unitSystem == UnitSystem.METRIC) {
            heightValues
        } else {
            heightValues.map { formatHeightForDisplay(it.toFloat(), unitSystem) }
        }
    }
    val weightUnit = weightUnitLabel(unitSystem)
    val heightUnit = heightUnitLabel(unitSystem)

    val steps = OnboardingStep.entries
    var currentStepIndex by rememberSaveable { mutableIntStateOf(0) }

    fun navigateToNextStep() {
        val nextIndex = currentStepIndex + 1
        if (nextIndex <= steps.lastIndex) {
            currentStepIndex = nextIndex
        }
    }

    fun navigateToPreviousStep() {
        if (currentStepIndex > 0) {
            currentStepIndex -= 1
        }
    }

    KalaiGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OnboardingTopBar(
                progress = (currentStepIndex + 1f) / steps.size.toFloat(),
                page = currentStepIndex,
                totalPages = steps.size,
                onBack = { navigateToPreviousStep() }
            )

            Box(modifier = Modifier.weight(1f)) {
                val isLastStep = currentStepIndex == steps.lastIndex
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(currentStepIndex) {
                                var totalDragX = 0f
                                detectHorizontalDragGestures(
                                    onHorizontalDrag = { _, dragAmount -> totalDragX += dragAmount },
                                    onDragEnd = {
                                        if (totalDragX <= -80f && currentStepIndex < steps.lastIndex) {
                                            navigateToNextStep()
                                        } else if (totalDragX >= 80f && currentStepIndex > 0) {
                                            navigateToPreviousStep()
                                        }
                                        totalDragX = 0f
                                    }
                                )
                            }
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            when (currentStepIndex) {
                                0 -> GenderOnboardingPage(
                                    selectedGender = uiState.gender,
                                    onSelected = onboardingViewModel::onGenderSelected
                                )
                                1 -> WeightOnboardingPage(
                                    values = displayWeightValues,
                                    selectedIndex = uiState.weightIndex,
                                    unitSuffix = weightUnit,
                                    onIndexChanged = onboardingViewModel::onWeightIndexChanged
                                )
                                2 -> HeightOnboardingPage(
                                    values = displayHeightValues,
                                    selectedIndex = uiState.heightIndex,
                                    unitSuffix = heightUnit,
                                    onIndexChanged = onboardingViewModel::onHeightIndexChanged
                                )
                                3 -> AgeOnboardingPage(
                                    values = ageValues,
                                    selectedIndex = uiState.ageIndex,
                                    onIndexChanged = onboardingViewModel::onAgeIndexChanged
                                )
                                4 -> ActivityOnboardingPage(
                                    selectedActivityLevel = uiState.activityLevel,
                                    onSelected = onboardingViewModel::onActivityLevelSelected
                                )
                                else -> GoalOnboardingPage(
                                    selectedGoal = uiState.goalChoice,
                                    onSelected = onboardingViewModel::onGoalSelected
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    KalaiButton(
                        onClick = {
                            if (isLastStep) {
                                onFinish(onboardingViewModel.buildResult())
                            } else {
                                navigateToNextStep()
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
        }
    }
}

@Composable
private fun OnboardingTopBar(
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
        OnboardingProgressBar(progress = progress)
    }
}
