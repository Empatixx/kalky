package cz.krokviak.kalai.home.repo

import cz.krokviak.kalai.common.DatabaseProvider

class NutrientSettingRepo {
    private val dao = DatabaseProvider.instance.nutrientSettingDao()

    fun getLatestSettings() = dao.getLatest()
}