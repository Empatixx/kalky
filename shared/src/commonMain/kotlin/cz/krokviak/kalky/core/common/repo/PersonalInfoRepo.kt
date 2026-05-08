package cz.krokviak.kalky.core.common.repo

import cz.krokviak.kalky.core.common.entities.PersonalInfoEntity
import cz.krokviak.kalky.core.db.KalkyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime

data class WeightEntry(
    val date: LocalDate,
    val weight: Double
)

open class PersonalInfoRepo(
    private val database: KalkyDatabase
) {
    private val queries get() = database.personalInfoQueries

    open suspend fun insertPersonalInfo(info: PersonalInfoEntity): Long = withContext(Dispatchers.IO) {
        database.transactionWithResult {
            queries.insertPersonalInfo(
                gender = info.gender,
                age = info.age,
                heightCm = info.heightCm,
                weightKg = info.weightKg,
                activityLevel = info.activityLevel,
                createdAt = info.createdAt.toString(),
                updatedAt = info.updatedAt.toString()
            )
            queries.lastInsertRowId().executeAsOne()
        }
    }

    open suspend fun getLatestPersonalInfo(): PersonalInfoEntity? = withContext(Dispatchers.IO) {
        queries.getLatestPersonalInfo().executeAsOneOrNull()?.toEntity()
    }

    open suspend fun getWeightsInRange(startDate: LocalDate, endDate: LocalDate): List<WeightEntry> = withContext(Dispatchers.IO) {
        val personalInfoList = queries.getPersonalInfoBetweenDates(
            startDate.toString(),
            endDate.toString()
        ).executeAsList()

        personalInfoList.map { row ->
            val instant = Instant.parse(row.createdAt)
            WeightEntry(
                date = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date,
                weight = row.weightKg.toDouble()
            )
        }
    }
}

private fun cz.krokviak.kalky.Personal_info.toEntity() = PersonalInfoEntity(
    id = id,
    gender = gender,
    age = age,
    heightCm = heightCm,
    weightKg = weightKg,
    activityLevel = activityLevel,
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt)
)
