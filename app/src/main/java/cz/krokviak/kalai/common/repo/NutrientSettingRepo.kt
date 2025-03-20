package cz.krokviak.kalai.common.repo

import cz.krokviak.kalai.common.entities.NutrientSettingEntity
import cz.krokviak.kalai.common.DatabaseProvider

class NutrientSettingRepo {
    private val dao = DatabaseProvider.instance.nutrientSettingDao()

    fun getLatestNutrientSettings() = dao.getLatestNutrientSettings()

    fun insertNutrientSettings(nutrientSettingEntity: NutrientSettingEntity) = dao.insertNutrientSettings(nutrientSettingEntity)

}