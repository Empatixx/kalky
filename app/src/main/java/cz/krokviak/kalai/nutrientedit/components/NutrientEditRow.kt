package cz.krokviak.kalai.nutrientedit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
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
import androidx.compose.ui.res.vectorResource
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
    value: Float,
    icon: ImageVector,
    activeColor: Color,
    onValueChange: (Float) -> Unit,
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
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                MacroNutrientDonutChart(
                    modifier = Modifier.fillMaxSize(),
                    percentage = 0.5f,
                    activeColor = activeColor,
                    centerIcon = icon,
                    centerIconSize = 32.dp,
                    holeRadius = 80f
                )
            }
            Column (
                modifier = Modifier.weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CupertinoText(
                    title,
                    fontSize = 16.sp
                )
                var v by remember { mutableStateOf(value) }
                CupertinoTextField(
                    value = v.toString(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        v = it.toFloat()
                    },
                    placeholder = {
                        CupertinoText(
                            text = "0",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                )
            }
        }
    }
}