package cz.krokviak.kalai.nutrientedit.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.R
import cz.krokviak.kalai.home.components.MacroNutrientDonutChart
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.CupertinoTextField
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

@Composable
fun NutrientEditRow(
    value: Int,
    icon: ImageVector,
    activeColor: Color,
    onValueChange: (Int) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
) {
    CupertinoSection(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp)),
        contentPadding = PaddingValues(0.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .size(100.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                MacroNutrientDonutChart(
                    modifier = Modifier.fillMaxSize(),
                    percentage = 0.5f,
                    activeColor = activeColor,
                    centerIcon = icon,
                    centerIconSize = 16.dp,
                    holeRadius = 80f
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CupertinoText(
                    title,
                    fontSize = 16.sp
                )
                var v by remember { mutableStateOf(value) }
                // Wrap the text field in a Box to overlay the pen icon and apply the background color.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = colorResource(id = R.color.lightBlueGray),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    CupertinoTextField(
                        singleLine = true,
                        value = v.toString(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        onValueChange = { newText ->
                            v = parseValue(newText, onValueChange)
                        },
                        placeholder = {
                            CupertinoText(
                                text = "0",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 40.dp) // Extra padding to avoid overlap with the pen icon
                    )
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                    )
                }
            }
        }
    }
}

// Extracted helper function that trims the input, safely parses it to an Int,
// and calls onValueChange with the parsed value.
private fun parseValue(input: String, onValueChange: (Int) -> Unit): Int {
    val value = input.trim().toIntOrNull() ?: 0
    onValueChange(value)
    return value
}

