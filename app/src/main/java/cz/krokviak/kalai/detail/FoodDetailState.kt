package cz.krokviak.kalai.detail

import org.threeten.bp.OffsetDateTime

data class FoodDetailState (
    val id: Long = 0,
    val name: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0,
    val portion: Int = 1,
    val healthScore: Int = 0,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
    val localImagePath: String? = null,
){

}