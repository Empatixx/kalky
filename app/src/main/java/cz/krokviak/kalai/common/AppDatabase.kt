package cz.krokviak.kalai.common

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cz.krokviak.kalai.camera.dao.FoodItemDao
import cz.krokviak.kalai.camera.dao.NutrientSettingsDao
import cz.krokviak.kalai.camera.entities.FoodItemEntity

@Database(entities = [FoodItemEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
    abstract fun nutrientSettingDao(): NutrientSettingsDao
}
