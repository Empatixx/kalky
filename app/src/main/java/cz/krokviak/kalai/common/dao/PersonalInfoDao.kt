// PersonalInfoDao.kt
package cz.krokviak.kalai.common.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cz.krokviak.kalai.common.entities.PersonalInfoEntity

@Dao
interface PersonalInfoDao {

    @Query("SELECT * FROM personal_info ORDER BY createdAt DESC LIMIT 1")
    fun getLatestPersonalInfo(): PersonalInfoEntity?

    @Insert
    fun insertPersonalInfo(personalInfo: PersonalInfoEntity): Long

    // 1) Add a new query to fetch all personal info rows within a date range
    @Query("""
        SELECT * 
        FROM personal_info
        WHERE date(createdAt) BETWEEN :startDate AND :endDate
        ORDER BY createdAt ASC
    """)
    fun getPersonalInfoBetweenDates(
        startDate: String,
        endDate: String
    ): List<PersonalInfoEntity>
}
