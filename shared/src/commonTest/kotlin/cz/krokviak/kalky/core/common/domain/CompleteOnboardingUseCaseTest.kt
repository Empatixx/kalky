package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.entities.Gender
import cz.krokviak.kalky.core.common.entities.NutrientSettingEntity
import cz.krokviak.kalky.core.common.entities.PersonalInfoEntity
import cz.krokviak.kalky.core.common.repo.PersonalInfoRepo
import cz.krokviak.kalky.scenes.onboarding.GoalChoice
import cz.krokviak.kalky.scenes.onboarding.OnboardingResult
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matching
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import dev.mokkery.verify.VerifyMode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class CompleteOnboardingUseCaseTest {

    private fun result(
        weight: String = "75.0",
        height: String = "180.0",
        age: String = "30",
        targetCalories: Int = 2000,
    ) = OnboardingResult(
        gender = Gender.MALE,
        weight = weight,
        height = height,
        age = age,
        activityLevel = 2,
        goal = GoalChoice.MAINTAIN,
        targetCalories = targetCalories,
        targetProtein = 150,
        targetCarbs = 200,
        targetFat = 70,
        promoCode = "",
    )

    @Test
    fun validInput_persistsPersonalInfo_andCallsUpdateNutrients() = runTest {
        val personalInfoRepo = mock<PersonalInfoRepo> {
            everySuspend { insertPersonalInfo(any()) } returns 1L
        }
        val updateNutrients = mock<UpdateNutrientSettingsUseCase> {
            everySuspend { invoke(any()) } returns Unit
        }
        val useCase = CompleteOnboardingUseCase(personalInfoRepo, updateNutrients)

        useCase(result())

        verifySuspend {
            personalInfoRepo.insertPersonalInfo(matching<PersonalInfoEntity> {
                it.weightKg == 75.0f && it.heightCm == 180.0f && it.age == 30
            })
        }
        verifySuspend {
            updateNutrients.invoke(matching<NutrientSettingEntity> {
                it.targetCalories == 2000 && it.targetProtein == 150
            })
        }
    }

    @Test
    fun zeroTargetCalories_skipsNutrientUpdate() = runTest {
        val personalInfoRepo = mock<PersonalInfoRepo> {
            everySuspend { insertPersonalInfo(any()) } returns 1L
        }
        val updateNutrients = mock<UpdateNutrientSettingsUseCase> {
            everySuspend { invoke(any()) } returns Unit
        }
        val useCase = CompleteOnboardingUseCase(personalInfoRepo, updateNutrients)

        useCase(result(targetCalories = 0))

        verifySuspend(VerifyMode.exactly(0)) { updateNutrients.invoke(any()) }
    }

    @Test
    fun malformedWeight_returnsEarly_persistsNothing() = runTest {
        val personalInfoRepo = mock<PersonalInfoRepo> {
            everySuspend { insertPersonalInfo(any()) } returns 1L
        }
        val updateNutrients = mock<UpdateNutrientSettingsUseCase> {
            everySuspend { invoke(any()) } returns Unit
        }
        val useCase = CompleteOnboardingUseCase(personalInfoRepo, updateNutrients)

        useCase(result(weight = "abc"))

        verifySuspend(VerifyMode.exactly(0)) { personalInfoRepo.insertPersonalInfo(any()) }
        verifySuspend(VerifyMode.exactly(0)) { updateNutrients.invoke(any()) }
    }

    @Test
    fun malformedAge_returnsEarly() = runTest {
        val personalInfoRepo = mock<PersonalInfoRepo> {
            everySuspend { insertPersonalInfo(any()) } returns 1L
        }
        val updateNutrients = mock<UpdateNutrientSettingsUseCase> {
            everySuspend { invoke(any()) } returns Unit
        }
        val useCase = CompleteOnboardingUseCase(personalInfoRepo, updateNutrients)

        useCase(result(age = "old"))

        verifySuspend(VerifyMode.exactly(0)) { personalInfoRepo.insertPersonalInfo(any()) }
    }
}
