package cz.krokviak.kalai.camera.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.io.Serializable

@Entity(tableName = "nutrient_settings")
data class NutrientSettingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val targetCalories: Int = 0,
    val targetProtein: Int = 0,
    val targetFat: Int = 0,
    val targetCarbs: Int = 0,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
)
