package cz.krokviak.kalky.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import cz.krokviak.kalky.onboarding.components.OnboardingProgressBar
import cz.krokviak.kalky.onboarding.pages.ActivityOnboardingPage
import cz.krokviak.kalky.onboarding.pages.AgeOnboardingPage
import cz.krokviak.kalky.onboarding.pages.AppearanceOnboardingPage
import cz.krokviak.kalky.onboarding.pages.GenderOnboardingPage
import cz.krokviak.kalky.onboarding.pages.GoalOnboardingPage
import cz.krokviak.kalky.onboarding.pages.HeightOnboardingPage
import cz.krokviak.kalky.onboarding.pages.LanguageOnboardingPage
import cz.krokviak.kalky.onboarding.pages.MacrosOnboardingPage
import cz.krokviak.kalky.onboarding.pages.PromoCodeOnboardingPage
import cz.krokviak.kalky.onboarding.pages.UnitsOnboardingPage
import cz.krokviak.kalky.onboarding.pages.WeightOnboardingPage
import cz.krokviak.kalky.common.AppLanguage
import cz.krokviak.kalky.common.AppPreferences
import cz.krokviak.kalky.common.UnitSystem
import cz.krokviak.kalky.di.koinInject
import cz.krokviak.kalky.theme.ThemeManager
import cz.krokviak.kalky.theme.ThemeMode
import cz.krokviak.kalky.settings.formatHeightForDisplay
import cz.krokviak.kalky.settings.formatWeightForDisplay
import cz.krokviak.kalky.settings.heightUnitLabel
import cz.krokviak.kalky.settings.weightUnitLabel
import cz.krokviak.kalky.theme.AppTheme
import cz.krokviak.kalky.ui.LocalDimensions
import cz.krokviak.kalky.ui.components.KalkyButton
import cz.krokviak.kalky.i18n.LocalStrings
import cz.krokviak.kalky.ui.components.KalkyGradientBackground

@Composable
fun OnboardingScene(
    onboardingViewModel: OnboardingViewModel,
    onFinish: (OnboardingResult) -> Unit,
    appPreferences: AppPreferences = koinInject()
) {
    val uiState by onboardingViewModel.uiState.collectAsState()
    val weightValues = onboardingViewModel.weightValues
    val heightValues = onboardingViewModel.heightValues
    val ageValues = onboardingViewModel.ageValues
    val language by appPreferences.language.collectAsState()
    val unitSystem by appPreferences.unitSystem.collectAsState()
    val themeMode by ThemeManager.themeMode.collectAsState()
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
            if (steps[nextIndex] == OnboardingStep.MACROS) {
                onboardingViewModel.calculateMacros()
            }
            currentStepIndex = nextIndex
        }
    }

    fun navigateToPreviousStep() {
        if (currentStepIndex > 0) {
            currentStepIndex -= 1
        }
    }

    val dims = LocalDimensions.current
    KalkyGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = dims.screenPadding, vertical = dims.itemSpacing),
            verticalArrangement = Arrangement.spacedBy(dims.itemSpacing)
        ) {
            OnboardingTopBar(
                progress = (currentStepIndex + 1f) / steps.size.toFloat(),
                page = currentStepIndex,
                totalPages = steps.size,
                onBack = { navigateToPreviousStep() }
            )

            Box(modifier = Modifier.weight(1f)) {
                val isLastStep = currentStepIndex == steps.lastIndex
                Column(modifier = Modifier.fillMaxSize()) {
                    val scrollState = rememberScrollState()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(scrollState),
                        contentAlignment = Alignment.Center
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
                                    0 -> LanguageOnboardingPage(
                                        selectedLanguage = language,
                                        onSelected = { appPreferences.setLanguage(it) }
                                    )
                                    1 -> UnitsOnboardingPage(
                                        selectedUnit = unitSystem,
                                        onSelected = { appPreferences.setUnitSystem(it) }
                                    )
                                    2 -> AppearanceOnboardingPage(
                                        selectedTheme = themeMode,
                                        onSelected = { ThemeManager.setThemeMode(it) }
                                    )
                                    3 -> GenderOnboardingPage(
                                        selectedGender = uiState.gender,
                                        onSelected = onboardingViewModel::onGenderSelected
                                    )
                                    4 -> WeightOnboardingPage(
                                        values = displayWeightValues,
                                        selectedIndex = uiState.weightIndex,
                                        unitSuffix = weightUnit,
                                        onIndexChanged = onboardingViewModel::onWeightIndexChanged
                                    )
                                    5 -> HeightOnboardingPage(
                                        values = displayHeightValues,
                                        selectedIndex = uiState.heightIndex,
                                        unitSuffix = heightUnit,
                                        onIndexChanged = onboardingViewModel::onHeightIndexChanged
                                    )
                                    6 -> AgeOnboardingPage(
                                        values = ageValues,
                                        selectedIndex = uiState.ageIndex,
                                        onIndexChanged = onboardingViewModel::onAgeIndexChanged
                                    )
                                    7 -> ActivityOnboardingPage(
                                        selectedActivityLevel = uiState.activityLevel,
                                        onSelected = onboardingViewModel::onActivityLevelSelected
                                    )
                                    8 -> GoalOnboardingPage(
                                        selectedGoal = uiState.goalChoice,
                                        onSelected = onboardingViewModel::onGoalSelected
                                    )
                                    9 -> MacrosOnboardingPage(
                                        protein = uiState.targetProtein,
                                        carbs = uiState.targetCarbs,
                                        fat = uiState.targetFat,
                                        onProteinChanged = onboardingViewModel::onProteinChanged,
                                        onCarbsChanged = onboardingViewModel::onCarbsChanged,
                                        onFatChanged = onboardingViewModel::onFatChanged
                                    )
                                    else -> PromoCodeOnboardingPage(
                                        promoCode = uiState.promoCode,
                                        onPromoCodeChange = onboardingViewModel::onPromoCodeChange
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    KalkyButton(
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
                        val s = LocalStrings.current
                        Text(
                            text = if (isLastStep) s.common.done else s.common.continueText,
                            fontWeight = FontWeight.Bold,
                            fontSize = dims.fontSubtitle
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
                contentDescription = LocalStrings.current.common.back,
                tint = if (page > 0) AppTheme.colors.onBackground else AppTheme.colors.onBackgroundSecondary.copy(alpha = 0.35f),
                modifier = Modifier.clickable(enabled = page > 0, onClick = onBack)
            )
            Text(
                text = "${page + 1}/$totalPages",
                color = AppTheme.colors.onBackgroundSecondary,
                fontSize = LocalDimensions.current.fontSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        OnboardingProgressBar(progress = progress)
    }
}
