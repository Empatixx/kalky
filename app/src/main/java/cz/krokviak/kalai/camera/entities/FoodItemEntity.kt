package cz.krokviak.kalai.camera.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime
import java.io.Serializable

@Entity(tableName = "food_items")
data class FoodItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carbs: Int,
    val portion: Int,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val localImagePath: String
) : Serializable