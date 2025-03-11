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
    val name: String = "Neznámé jídlo",
    val calories: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0,
    val portion: Int = 1,
    val healthScore: Int = 0,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
    val localImagePath: String,
    val loading: Boolean = true
) : Serializable
