package cz.krokviak.kalai.common.repo

import android.content.Context
import cz.krokviak.kalai.common.DatabaseProvider
import cz.krokviak.kalai.common.dao.PersonalInfoDao
import cz.krokviak.kalai.common.entities.PersonalInfoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class PersonalInfoRepo(
    context: Context
) {
    private val personalInfoDao: PersonalInfoDao = DatabaseProvider.instance.personalInfoDao()

    /**
     * Insert a new [PersonalInfoEntity] record (e.g., for a new weigh-in).
     */
    suspend fun insertPersonalInfo(info: PersonalInfoEntity): Long = withContext(Dispatchers.IO) {
        personalInfoDao.insertPersonalInfo(info)
    }

    /**
     * Fetch [PersonalInfoEntity] records for the last 7 days and convert them to a list of weights.
     */
    suspend fun getWeightsForLast7Days(): List<Double> = withContext(Dispatchers.IO) {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(6) // 7-day range

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val startDateStr = startDate.format(formatter)
        val endDateStr = endDate.format(formatter)

        val personalInfoList = personalInfoDao.getPersonalInfoBetweenDates(
            startDateStr,
            endDateStr
        )

        // Convert each entity's weightKg (Float) into a Double for your chart:
        personalInfoList.map { it.weightKg.toDouble() }
    }
}
