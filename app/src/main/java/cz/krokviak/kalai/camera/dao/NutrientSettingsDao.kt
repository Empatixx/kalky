package cz.krokviak.kalai.camera.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cz.krokviak.kalai.camera.entities.NutrientSettingEntity

@Dao
interface NutrientSettingsDao{
    @Insert
    fun insert(nutrientSetting: NutrientSettingEntity)

    @Insert
    fun insertAll(nutrientSettings: List<NutrientSettingEntity>)

    @Query("SELECT * FROM nutrient_settings")
    fun getAll(): List<NutrientSettingEntity>

    @Query("SELECT * FROM nutrient_settings WHERE id = :id")
    fun getById(id: Long): NutrientSettingEntity

    @Query("SELECT * FROM nutrient_settings ORDER BY createdAt DESC LIMIT 1")
    fun getLatest(): NutrientSettingEntity
}
