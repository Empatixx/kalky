package cz.krokviak.kalai.onboarding

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cz.krokviak.kalai.onboarding.components.OnboardingProgressBar
import cz.krokviak.kalai.onboarding.pages.ActivityOnboardingPage
import cz.krokviak.kalai.onboarding.pages.AgeOnboardingPage
import cz.krokviak.kalai.onboarding.pages.GenderOnboardingPage
import cz.krokviak.kalai.onboarding.pages.GoalOnboardingPage
import cz.krokviak.kalai.onboarding.pages.HeightOnboardingPage
import cz.krokviak.kalai.onboarding.pages.WeightOnboardingPage
import cz.krokviak.kalai.theme.AppTheme
import cz.krokviak.kalai.ui.components.KalaiButton

@Composable
fun OnboardingPage(
    onboardingViewModel: OnboardingViewModel,
    onFinish: (OnboardingResult) -> Unit
) {
    val uiState by onboardingViewModel.uiState.collectAsState()
    val weightValues = onboardingViewModel.weightValues
    val heightValues = onboardingViewModel.heightValues
    val ageValues = onboardingViewModel.ageValues

    val steps = OnboardingStepRoute.entries
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: OnboardingStepRoute.GENDER.route
    val currentStep = steps.firstOrNull { it.route == currentRoute } ?: OnboardingStepRoute.GENDER
    val currentStepIndex = steps.indexOf(currentStep).coerceAtLeast(0)

    fun navigateToNextStep() {
        val nextIndex = currentStepIndex + 1
        if (nextIndex <= steps.lastIndex) {
            navController.navigate(steps[nextIndex].route) { launchSingleTop = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OnboardingTopBar(
            progress = (currentStepIndex + 1f) / steps.size.toFloat(),
            page = currentStepIndex,
            totalPages = steps.size,
            onBack = {
                if (currentStepIndex > 0) {
                    navController.popBackStack()
                }
            }
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .pointerInput(currentStepIndex) {
                    var totalDragX = 0f
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount -> totalDragX += dragAmount },
                        onDragEnd = {
                            if (totalDragX <= -80f && currentStepIndex < steps.lastIndex) {
                                navigateToNextStep()
                            } else if (totalDragX >= 80f && currentStepIndex > 0) {
                                navController.popBackStack()
                            }
                        }
                    )
                }
        ) {
            NavHost(
                navController = navController,
                startDestination = OnboardingStepRoute.GENDER.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(OnboardingStepRoute.GENDER.route) {
                    GenderOnboardingPage(
                        selectedGender = uiState.gender,
                        onSelected = onboardingViewModel::onGenderSelected
                    )
                }
                composable(OnboardingStepRoute.WEIGHT.route) {
                    WeightOnboardingPage(
                        values = weightValues,
                        selectedIndex = uiState.weightIndex,
                        onIndexChanged = onboardingViewModel::onWeightIndexChanged
                    )
                }
                composable(OnboardingStepRoute.HEIGHT.route) {
                    HeightOnboardingPage(
                        values = heightValues,
                        selectedIndex = uiState.heightIndex,
                        onIndexChanged = onboardingViewModel::onHeightIndexChanged
                    )
                }
                composable(OnboardingStepRoute.AGE.route) {
                    AgeOnboardingPage(
                        values = ageValues,
                        selectedIndex = uiState.ageIndex,
                        onIndexChanged = onboardingViewModel::onAgeIndexChanged
                    )
                }
                composable(OnboardingStepRoute.ACTIVITY.route) {
                    ActivityOnboardingPage(
                        selectedActivityLevel = uiState.activityLevel,
                        onSelected = onboardingViewModel::onActivityLevelSelected
                    )
                }
                composable(OnboardingStepRoute.GOAL.route) {
                    GoalOnboardingPage(
                        selectedGoal = uiState.goalChoice,
                        onSelected = onboardingViewModel::onGoalSelected
                    )
                }
            }
        }

        val isLastStep = currentStepIndex == steps.lastIndex
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
