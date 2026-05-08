package cz.krokviak.kalky.scenes.onboarding

import app.cash.turbine.test
import cz.krokviak.kalky.core.common.entities.PersonalInfoEntity
import cz.krokviak.kalky.core.common.repo.PersonalInfoRepo
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() { Dispatchers.setMain(dispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    private fun emptyRepo() = mock<PersonalInfoRepo> {
        everySuspend { getLatestPersonalInfo() } returns null
    }

    @Test
    fun init_withNoPersonalInfo_keepsDefaults() = runTest(dispatcher) {
        val vm = OnboardingViewModel(emptyRepo())
        advanceUntilIdle()
        val s = vm.uiState.value
        assertEquals("Muž", s.gender)
        assertEquals(2, s.activityLevel)
        assertEquals(GoalChoice.MAINTAIN, s.goalChoice)
    }

    @Test
    fun init_withExistingPersonalInfo_restoresValues() = runTest(dispatcher) {
        val info = PersonalInfoEntity(
            weightKg = 75.5f, heightCm = 175.0f, age = 28, gender = "Žena", activityLevel = 3
        )
        val repo = mock<PersonalInfoRepo> {
            everySuspend { getLatestPersonalInfo() } returns info
        }
        val vm = OnboardingViewModel(repo)
        advanceUntilIdle()
        val s = vm.uiState.value
        assertEquals("Žena", s.gender)
        assertEquals(3, s.activityLevel)
        // weightIndex 0 = 30.0kg, so 75.5 -> index (755-300)=455
        assertEquals(455, s.weightIndex)
        // heightIndex 0 = 100cm, 175 -> 75
        assertEquals(75, s.heightIndex)
        // ageIndex 0 = 1, age 28 -> 27
        assertEquals(27, s.ageIndex)
    }

    @Test
    fun init_clampsActivityLevel_toValidRange() = runTest(dispatcher) {
        val info = PersonalInfoEntity(weightKg = 70f, heightCm = 175f, age = 30,
            gender = "Muž", activityLevel = 99)
        val repo = mock<PersonalInfoRepo> { everySuspend { getLatestPersonalInfo() } returns info }
        val vm = OnboardingViewModel(repo)
        advanceUntilIdle()
        assertEquals(4, vm.uiState.value.activityLevel)
    }

    @Test
    fun onGenderSelected_updatesState() {
        val vm = OnboardingViewModel(emptyRepo())
        vm.onGenderSelected("Žena")
        assertEquals("Žena", vm.uiState.value.gender)
    }

    @Test
    fun onActivityLevelSelected_clampsToOneToFour() {
        val vm = OnboardingViewModel(emptyRepo())
        vm.onActivityLevelSelected(0)
        assertEquals(1, vm.uiState.value.activityLevel)
        vm.onActivityLevelSelected(99)
        assertEquals(4, vm.uiState.value.activityLevel)
    }

    @Test
    fun onGoalSelected_updatesGoal() {
        val vm = OnboardingViewModel(emptyRepo())
        vm.onGoalSelected(GoalChoice.LOSE)
        assertEquals(GoalChoice.LOSE, vm.uiState.value.goalChoice)
    }

    @Test
    fun onWeightHeightAgeIndexChanged_clampsToValidRange() {
        val vm = OnboardingViewModel(emptyRepo())
        vm.onWeightIndexChanged(-5)
        assertEquals(0, vm.uiState.value.weightIndex)
        vm.onHeightIndexChanged(99999)
        assertEquals(vm.heightValues.lastIndex, vm.uiState.value.heightIndex)
        vm.onAgeIndexChanged(50)
        assertEquals(50, vm.uiState.value.ageIndex)
    }

    @Test
    fun onPromoCodeChange_storesCode() {
        val vm = OnboardingViewModel(emptyRepo())
        vm.onPromoCodeChange("KALKY10")
        assertEquals("KALKY10", vm.uiState.value.promoCode)
    }

    @Test
    fun onProteinCarbsFatChanged_aggregateCalories() {
        val vm = OnboardingViewModel(emptyRepo())
        vm.onProteinChanged(50)  // 200 kcal
        vm.onCarbsChanged(100)   // +400
        vm.onFatChanged(20)      // +180
        assertEquals(780, vm.uiState.value.targetCalories)
    }

    @Test
    fun calculateMacros_male_maintainGoal_setsTargetsFromMifflinStJeor() {
        val vm = OnboardingViewModel(emptyRepo())
        // weightIndex 500 -> 80kg, heightIndex 70 -> 170cm, ageIndex 24 -> 25 yrs
        vm.calculateMacros()
        val s = vm.uiState.value
        // BMR(M) = 10*80 + 6.25*170 - 5*25 + 5 = 800 + 1062.5 - 125 + 5 = 1742.5
        // Activity 2 -> *1.375 = 2395.9, MAINTAIN -> 2396
        assertEquals(2396, s.targetCalories)
        assertTrue(s.targetProtein > 0)
        assertTrue(s.targetCarbs > 0)
        assertTrue(s.targetFat > 0)
    }

    @Test
    fun calculateMacros_loseGoal_subtracts500FromTdee() {
        val vm = OnboardingViewModel(emptyRepo())
        vm.onGoalSelected(GoalChoice.LOSE)
        vm.calculateMacros()
        val s = vm.uiState.value
        // 2396 - 500 = 1896
        assertEquals(1896, s.targetCalories)
    }

    @Test
    fun calculateMacros_gainGoal_adds300ToTdee() {
        val vm = OnboardingViewModel(emptyRepo())
        vm.onGoalSelected(GoalChoice.GAIN)
        vm.calculateMacros()
        // 2396 + 300 = 2696
        assertEquals(2696, vm.uiState.value.targetCalories)
    }

    @Test
    fun submit_emitsCompletedResult() = runTest(dispatcher) {
        val vm = OnboardingViewModel(emptyRepo())
        advanceUntilIdle()
        vm.calculateMacros()

        vm.completed.test {
            vm.submit()
            advanceUntilIdle()
            val result = awaitItem()
            assertEquals("Muž", result.gender)
            assertEquals(GoalChoice.MAINTAIN, result.goal)
            assertTrue(result.targetCalories > 0)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
