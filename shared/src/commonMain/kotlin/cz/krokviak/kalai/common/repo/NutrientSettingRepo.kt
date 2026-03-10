package cz.krokviak.kalai.common.repo

import cz.krokviak.kalai.common.entities.NutrientSettingEntity
import cz.krokviak.kalai.db.KalaiDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

class NutrientSettingRepo(
    private val database: KalaiDatabase
) {
    private val queries get() = database.nutrientSettingQueries

    suspend fun getLatestNutrientSettings(): NutrientSettingEntity? = withContext(Dispatchers.IO) {
        queries.getLatestNutrientSettings().executeAsOneOrNull()?.toEntity()
    }

    suspend fun insertNutrientSettings(entity: NutrientSettingEntity) = withContext(Dispatchers.IO) {
        queries.insertNutrientSettings(
            targetCalories = entity.targetCalories,
            targetProtein = entity.targetProtein,
            targetFat = entity.targetFat,
            targetCarbs = entity.targetCarbs,
            createdAt = entity.createdAt.toString(),
            updatedAt = entity.updatedAt.toString()
        )
    }
}

private fun cz.krokviak.kalai.Nutrient_settings.toEntity() = NutrientSettingEntity(
    id = id,
    targetCalories = targetCalories,
    targetProtein = targetProtein,
    targetFat = targetFat,
    targetCarbs = targetCarbs,
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt)
)
