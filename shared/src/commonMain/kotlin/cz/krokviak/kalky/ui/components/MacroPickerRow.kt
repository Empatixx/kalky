package cz.krokviak.kalky.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import cz.krokviak.kalky.nutrientedit.components.NutrientEditRow
import cz.krokviak.kalky.settings.components.IosInlineValuePicker

/**
 * NutrientEditRow + IosInlineValuePicker combined for the protein/carbs/fat triplet pattern.
 *
 * Use one MacroPickerRow per macro. The caller owns the `expanded` flag (typically a
 * single `activeField: MacroField?` shared across the three rows so only one picker is
 * open at a time) and the value-change callback.
 */
@Composable
fun MacroPickerRow(
    label: String,
    value: Int,
    icon: ImageVector,
    activeColor: Color,
    expanded: Boolean,
    onClick: () -> Unit,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    valueUnit: String = "g",
    minValue: Int = 0,
    maxValue: Int = 500,
    enabled: Boolean = true
) {
    val values = remember(minValue, maxValue) {
        (minValue..maxValue).map { it.toString() }
    }
    val selectedIndex = (value - minValue).coerceIn(0, values.lastIndex)

    NutrientEditRow(
        label = label,
        value = value,
        valueUnit = valueUnit,
        icon = icon,
        activeColor = activeColor,
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled
    )
    if (expanded) {
        IosInlineValuePicker(
            values = values,
            selectedIndex = selectedIndex,
            onIndexChanged = { onValueChange(it + minValue) },
            unitSuffix = valueUnit
        )
    }
}
