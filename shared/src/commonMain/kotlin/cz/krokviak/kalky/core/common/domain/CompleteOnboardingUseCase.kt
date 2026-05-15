package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.entities.NutrientSettingEntity
import cz.krokviak.kalky.core.common.entities.PersonalInfoEntity
import cz.krokviak.kalky.core.common.repo.PersonalInfoRepo
import cz.krokviak.kalky.scenes.onboarding.OnboardingResult

open class CompleteOnboardingUseCase(
    private val personalInfoRepo: PersonalInfoRepo,
    private val updateNutrientSettings: UpdateNutrientSettingsUseCase,
) {
    open suspend operator fun invoke(result: OnboardingResult) {
        val weight = result.weight.toFloatOrNull() ?: return
        val height = result.height.toFloatOrNull() ?: return
        val age = result.age.toIntOrNull() ?: return

        personalInfoRepo.insertPersonalInfo(
            PersonalInfoEntity(
                weightKg = weight,
                heightCm = height,
                age = age,
                gender = result.gender,
                activityLevel = result.activityLevel,
            )
        )

        if (result.targetCalories > 0) {
            updateNutrientSettings(
                NutrientSettingEntity(
                    targetCalories = result.targetCalories,
                    targetProtein = result.targetProtein,
                    targetCarbs = result.targetCarbs,
                    targetFat = result.targetFat,
                )
            )
        }
    }
}
