package cz.krokviak.kalky.common.domain

import cz.krokviak.kalky.common.entities.NutrientSettingEntity
import cz.krokviak.kalky.common.entities.PersonalInfoEntity
import cz.krokviak.kalky.common.repo.PersonalInfoRepo
import cz.krokviak.kalky.onboarding.OnboardingResult

/**
 * Persists everything the user picked during onboarding: a PersonalInfoEntity
 * snapshot and (when calorie targets were set) a NutrientSettingEntity.
 *
 * Centralizes the cross-feature write so AppContent doesn't need to know the
 * internals of SettingsViewModel / NutrientEditViewModel / MainViewModel.
 */
class CompleteOnboardingUseCase(
    private val personalInfoRepo: PersonalInfoRepo,
    private val updateNutrientSettings: UpdateNutrientSettingsUseCase,
) {
    suspend operator fun invoke(result: OnboardingResult) {
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
