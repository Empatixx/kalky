package cz.krokviak.kalai.home.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit


@Composable
fun CupertinoAutoscaleText(
    text: String,
    style: TextStyle = LocalTextStyle.current,
    maxLines: Int = 1,
    fontSize: TextUnit,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    stepFactor: Float = 0.95f
) {
    var currentFontSize by remember { mutableStateOf(fontSize) }
    var readyToDraw by remember { mutableStateOf(false) }
        Text(
            text = text,
            maxLines = maxLines,
            color = color,
            fontWeight = fontWeight,
            style = style.copy(fontSize = currentFontSize),
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.hasVisualOverflow) {
                    currentFontSize = (currentFontSize * stepFactor)
                } else {
                    readyToDraw = true
                }
            },
            // Only draw the text when we're sure it fits within the bounds
            modifier = if (readyToDraw) Modifier else Modifier.drawWithContent { }
        )
}
