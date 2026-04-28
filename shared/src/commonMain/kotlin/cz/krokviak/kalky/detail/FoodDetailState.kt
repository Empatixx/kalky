package cz.krokviak.kalky.detail

import androidx.compose.runtime.Immutable
import cz.krokviak.kalky.common.error.UiError
import cz.krokviak.kalky.nutrientedit.MacroField
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Immutable
data class FoodDetailState(
    val id: Long = 0,
    val name: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0,
    val portion: Int = 1,
    val healthScore: Int = 0,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
    val localImagePath: String? = null,
    val activeField: MacroField? = null,
    val error: UiError? = null,
)
