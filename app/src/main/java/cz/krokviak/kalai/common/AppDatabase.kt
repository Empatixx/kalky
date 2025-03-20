package cz.krokviak.kalai.common

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cz.krokviak.kalai.common.dao.FoodItemDao
import cz.krokviak.kalai.common.dao.NutrientSettingsDao
import cz.krokviak.kalai.common.dao.PersonalInfoDao
import cz.krokviak.kalai.common.entities.FoodItemEntity
import cz.krokviak.kalai.common.entities.NutrientSettingEntity
import cz.krokviak.kalai.common.entities.PersonalInfoEntity

@Database(entities = [FoodItemEntity::class, NutrientSettingEntity::class, PersonalInfoEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
    abstract fun nutrientSettingDao(): NutrientSettingsDao
    abstract fun personalInfoDao(): PersonalInfoDao
}
