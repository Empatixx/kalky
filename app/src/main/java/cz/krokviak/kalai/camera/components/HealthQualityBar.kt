package cz.krokviak.kalai.camera.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import cz.krokviak.kalai.R

@Composable
fun HealthQualityBar(
    score: Int = 7,
    maxScore: Int = 10,
    rectCount: Int = 10
) {
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.HeartBroken,
                    contentDescription = "Heart",
                    modifier = Modifier.size(48.dp),
                    tint = colorResource(id = R.color.sugarColor)
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Health score",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$score/$maxScore",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (i in 1..rectCount) {
                        // highlight up to (score * rectCount / maxScore)
                        val threshold = score * rectCount / maxScore
                        Box(
                            modifier = Modifier
                                .size(width = 16.dp, height = 8.dp)
                                .background(
                                    if (i <= threshold) Color.Green else Color.LightGray
                                )
                        )
                    }
                }
            }
        }
    }
}