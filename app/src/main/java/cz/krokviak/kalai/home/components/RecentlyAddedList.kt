package cz.krokviak.kalai.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import cz.krokviak.kalai.camera.entities.FoodItemEntity

@Composable
fun RecentlyAddedList(
    items: List<FoodItemEntity>,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) {
        EmptyRecentlyAddedList()
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                FoodItemCard(item)
            }
        }
    }
}

@Composable
fun EmptyRecentlyAddedList() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Dneska jsi ještě nic nepřidal/a",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Klikni na tlačítko dole a přidej si první jídlo",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
