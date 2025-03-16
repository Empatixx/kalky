package cz.krokviak.kalai.camera.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cz.krokviak.kalai.camera.entities.NutrientSettingEntity

@Dao
interface NutrientSettingsDao{
    @Query("SELECT * from nutrient_settings ORDER BY id DESC LIMIT 1")
    fun getLatestNutrientSettings(): NutrientSettingEntity?

    @Insert
    fun insertNutrientSettings(nutrientSettingEntity: NutrientSettingEntity)
}
