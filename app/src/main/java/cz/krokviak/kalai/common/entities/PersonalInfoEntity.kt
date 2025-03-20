package cz.krokviak.kalai.common.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.io.Serializable

@Entity(tableName = "personal_info")
data class PersonalInfoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val gender: String,            // e.g. "Male", "Female", or "Other"
    val age: Int,                  // e.g. user's age in years
    val heightCm: Float,           // e.g. height in centimeters
    val weightKg: Float,           // e.g. current weight in kilograms
    val activityLevel: Int,        // e.g. an integer representing sedentary(1), lightly active(2), etc.
    
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
) : Serializable
