package cz.krokviak.kalai.nutrientedit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.alexzhirkevich.LocalTextStyle
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.CupertinoTextField
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

import androidx.compose.ui.text.style.TextAlign

@Composable
fun NutrientEditRow(
    value: Int,
    valueUnit: String,
    icon: ImageVector,
    activeColor: Color,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var v by remember { mutableStateOf(value) }

    CupertinoSection(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp)),
        contentPadding = PaddingValues(0.dp),
        dividerPadding = PaddingValues(0.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Icon Column with fixed size
            Column(
                modifier = Modifier
                    .size(50.dp)
                    .background(activeColor),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                CupertinoIcon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp).align(Alignment.CenterHorizontally)
                )
            }
            // TextField Column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp), // optional padding on the row
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    // 1) Text field, weighted to fill remaining space
                    BasicTextField(
                        value = v.toString(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        onValueChange = { newText ->
                            v = parseValue(newText, onValueChange)
                        },
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )

                    CupertinoText(
                        text = valueUnit,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun parseValue(input: String, onValueChange: (Int) -> Unit): Int {
    val value = input.trim().toIntOrNull() ?: 0
    onValueChange(value)
    return value
}
