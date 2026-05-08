package cz.krokviak.kalky.core.common.domain

import cz.krokviak.kalky.core.common.entities.NutrientSettingEntity
import cz.krokviak.kalky.core.common.repo.NutrientSettingRepo

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
