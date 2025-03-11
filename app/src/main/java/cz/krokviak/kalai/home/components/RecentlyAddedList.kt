package cz.krokviak.kalai.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cz.krokviak.kalai.camera.entities.FoodItemEntity
import cz.krokviak.kalai.home.MainViewModel
import io.github.alexzhirkevich.cupertino.CupertinoText
import io.github.alexzhirkevich.cupertino.section.CupertinoSection

@Composable
fun RecentlyAddedList(
    items: List<FoodItemEntity>,
    progreeses: Map<Long, Int>,
    modifier: Modifier = Modifier
) {
    CupertinoText(
        text = "Nedávno přidané",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(16.dp))

    if (items.isEmpty()) {
        EmptyRecentlyAddedList()
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                FoodItemCard(item, progreeses[item.id] ?: 0)
            }
        }
    }
}

@Composable
fun EmptyRecentlyAddedList() {
    CupertinoSection(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CupertinoText(
                text = "Dneska jsi ještě nic nepřidal/a",
                style = MaterialTheme.typography.titleMedium
            )
            CupertinoText(
                text = "Klikni na tlačítko dole a přidej si první jídlo",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
