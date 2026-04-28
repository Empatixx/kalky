package cz.krokviak.kalky.common.domain

import cz.krokviak.kalky.common.entities.NutrientSettingEntity
import cz.krokviak.kalky.common.repo.NutrientSettingRepo

class UpdateNutrientSettingsUseCase(
    private val nutrientSettingRepo: NutrientSettingRepo,
) {
    suspend operator fun invoke(entity: NutrientSettingEntity) {
        nutrientSettingRepo.insertNutrientSettings(entity)
    }
}

class GetLatestNutrientSettingsUseCase(
    private val nutrientSettingRepo: NutrientSettingRepo,
) {
    suspend operator fun invoke(): NutrientSettingEntity? =
        nutrientSettingRepo.getLatestNutrientSettings()
}
