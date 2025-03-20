package cz.krokviak.kalai.common.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cz.krokviak.kalai.common.entities.NutrientSettingEntity

@Dao
interface NutrientSettingsDao{
    @Query("SELECT * from nutrient_settings ORDER BY createdAt DESC LIMIT 1")
    fun getLatestNutrientSettings(): NutrientSettingEntity?

    @Insert
    fun insertNutrientSettings(nutrientSettingEntity: NutrientSettingEntity)

}
