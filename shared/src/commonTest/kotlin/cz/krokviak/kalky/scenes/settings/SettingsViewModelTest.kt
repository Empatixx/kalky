package cz.krokviak.kalky.scenes.settings

import cz.krokviak.kalky.core.common.entities.Gender
import cz.krokviak.kalky.core.common.entities.PersonalInfoEntity
import cz.krokviak.kalky.core.common.repo.PersonalInfoRepo
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import dev.mokkery.verify.VerifyMode
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() { Dispatchers.setMain(dispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    private fun emptyRepo() = mock<PersonalInfoRepo> {
        everySuspend { getLatestPersonalInfo() } returns null
        everySuspend { insertPersonalInfo(any()) } returns 1L
    }

    @Test
    fun init_loadsPersonalInfo_intoState() = runTest(dispatcher) {
        val info = PersonalInfoEntity(
            weightKg = 75.5f, heightCm = 175.0f, age = 30, gender = Gender.FEMALE, activityLevel = 3
        )
        val repo = mock<PersonalInfoRepo> {
            everySuspend { getLatestPersonalInfo() } returns info
            everySuspend { insertPersonalInfo(any()) } returns 1L
        }
        val vm = SettingsViewModel(repo)
        advanceUntilIdle()
        val s = vm.uiState.value
        assertEquals("75.5", s.weight)
        assertEquals("175.0", s.height)
        assertEquals("30", s.age)
        assertEquals(Gender.FEMALE, s.gender)
        assertEquals(3, s.activityLevel)
    }

    @Test
    fun init_withNoPersonalInfo_keepsDefaults() = runTest(dispatcher) {
        val vm = SettingsViewModel(emptyRepo())
        advanceUntilIdle()
        val s = vm.uiState.value
        assertEquals("", s.weight)
        assertEquals(Gender.MALE, s.gender)
    }

    @Test
    fun onWeightChange_updatesWeight_andResetsSavedFlag() = runTest(dispatcher) {
        val vm = SettingsViewModel(emptyRepo())
        advanceUntilIdle()
        vm.onWeightChange("80.0")
        assertEquals("80.0", vm.uiState.value.weight)
        assertTrue(!vm.uiState.value.saved)
    }

    @Test
    fun onHeightAndAgeChange_updatesValues() {
        val vm = SettingsViewModel(emptyRepo())
        vm.onHeightChange("180.0")
        vm.onAgeChange("28")
        assertEquals("180.0", vm.uiState.value.height)
        assertEquals("28", vm.uiState.value.age)
    }

    @Test
    fun onGenderChange_andOnActivityLevelChange_updateState() {
        val vm = SettingsViewModel(emptyRepo())
        vm.onGenderChange(Gender.FEMALE)
        vm.onActivityLevelChange(4)
        assertEquals(Gender.FEMALE, vm.uiState.value.gender)
        assertEquals(4, vm.uiState.value.activityLevel)
    }

    @Test
    fun togglePickerField_setsAndClears() {
        val vm = SettingsViewModel(emptyRepo())
        vm.togglePickerField(ProfilePickerField.WEIGHT)
        assertEquals(ProfilePickerField.WEIGHT, vm.uiState.value.activePickerField)
        vm.togglePickerField(ProfilePickerField.WEIGHT)
        assertNull(vm.uiState.value.activePickerField)
    }

    @Test
    fun togglePickerField_switchesBetweenFields() {
        val vm = SettingsViewModel(emptyRepo())
        vm.togglePickerField(ProfilePickerField.WEIGHT)
        vm.togglePickerField(ProfilePickerField.HEIGHT)
        assertEquals(ProfilePickerField.HEIGHT, vm.uiState.value.activePickerField)
    }

    @Test
    fun save_validInput_persistsToRepoAndSetsSavedTrue() = runTest(dispatcher) {
        val repo = mock<PersonalInfoRepo> {
            everySuspend { getLatestPersonalInfo() } returns null
            everySuspend { insertPersonalInfo(any()) } returns 1L
        }
        val vm = SettingsViewModel(repo)
        advanceUntilIdle()
        vm.onWeightChange("80")
        vm.onHeightChange("180")
        vm.onAgeChange("30")
        vm.save()
        advanceUntilIdle()

        verifySuspend { repo.insertPersonalInfo(any()) }
        assertTrue(vm.uiState.value.saved)
    }

    @Test
    fun save_invalidWeight_doesNotPersist() = runTest(dispatcher) {
        val repo = mock<PersonalInfoRepo> {
            everySuspend { getLatestPersonalInfo() } returns null
            everySuspend { insertPersonalInfo(any()) } returns 1L
        }
        val vm = SettingsViewModel(repo)
        advanceUntilIdle()
        vm.onWeightChange("not-a-number")
        vm.onHeightChange("180")
        vm.onAgeChange("30")
        vm.save()
        advanceUntilIdle()

        verifySuspend(VerifyMode.exactly(0)) { repo.insertPersonalInfo(any()) }
    }

    @Test
    fun bmi_returnsNull_whenInputInvalid() {
        val vm = SettingsViewModel(emptyRepo())
        vm.onWeightChange("xx")
        vm.onHeightChange("180")
        assertNull(vm.uiState.value.bmi)
    }

    @Test
    fun bmi_calculatesCorrectly_forValidInputs() {
        val vm = SettingsViewModel(emptyRepo())
        vm.onWeightChange("80")
        vm.onHeightChange("180")

        val bmi = vm.uiState.value.bmi!!
        assertTrue(bmi > 24.5f && bmi < 24.8f, "expected ~24.69, got $bmi")
    }
}
