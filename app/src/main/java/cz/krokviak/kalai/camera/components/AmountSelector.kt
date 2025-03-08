package cz.krokviak.kalai.camera.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

@Composable
fun AmountSelector(
    amount: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDecrease) {
                Icon(
                    imageVector = Icons.Outlined.Remove,
                    contentDescription = "Decrease",
                    modifier = Modifier.size(16.dp)
                )
            }
            Text("$amount")
            IconButton(onClick = onIncrease) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Increase",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
