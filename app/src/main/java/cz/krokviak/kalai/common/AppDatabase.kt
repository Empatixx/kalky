package cz.krokviak.kalai.common

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cz.krokviak.kalai.camera.dao.FoodItemDao
import cz.krokviak.kalai.camera.dao.NutrientSettingsDao
import cz.krokviak.kalai.camera.entities.FoodItemEntity
import cz.krokviak.kalai.camera.entities.NutrientSettingEntity

@Database(entities = [FoodItemEntity::class, NutrientSettingEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
    abstract fun nutrientSettingDao(): NutrientSettingsDao
}
