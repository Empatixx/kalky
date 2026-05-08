package cz.krokviak.kalky.core.common.repo

import cz.krokviak.kalky.core.common.entities.NutrientSettingEntity
import cz.krokviak.kalky.core.db.KalkyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

interface NutrientSettingRepo {
    suspend fun getLatestNutrientSettings(): NutrientSettingEntity?
    suspend fun insertNutrientSettings(entity: NutrientSettingEntity)
}

class NutrientSettingRepoImpl(
    private val database: KalkyDatabase
) : NutrientSettingRepo {
    private val queries get() = database.nutrientSettingQueries

    override suspend fun getLatestNutrientSettings(): NutrientSettingEntity? = withContext(Dispatchers.IO) {
        queries.getLatestNutrientSettings().executeAsOneOrNull()?.toEntity()
    }

    override suspend fun insertNutrientSettings(entity: NutrientSettingEntity) = withContext(Dispatchers.IO) {
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

private fun cz.krokviak.kalky.Nutrient_settings.toEntity() = NutrientSettingEntity(
    id = id,
    targetCalories = targetCalories,
    targetProtein = targetProtein,
    targetFat = targetFat,
    targetCarbs = targetCarbs,
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt)
)
