
package com.fabiobassi.famigliab.ui.features.poop_tracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.PoopEntry
import com.fabiobassi.famigliab.data.SettingsDataStore

@Composable
fun PoopEntryItem(entry: PoopEntry) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val personColorHex by settingsDataStore.getColorFor(entry.person.name)
        .collectAsState(initial = "")

    val backgroundColor = if (personColorHex.isNotEmpty()) {
        Color(android.graphics.Color.parseColor(personColorHex))
    } else {
        Color.Transparent
    }
    Card(modifier = Modifier.padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (entry.quality == "Good")
                    Icon(Icons.Filled.ThumbUp, contentDescription = "Good poop")
                else
                    Icon(Icons.Filled.ThumbDown, contentDescription = "Bad poop")
                Spacer(modifier = Modifier.weight(0.1f))
                Text(
                    text = "${entry.date} - ${entry.hour}",
                    modifier = Modifier.weight(4f),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = entry.person.name,
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = backgroundColor,
                            shape = CircleShape,
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    textAlign = TextAlign.Center,
                    color = if (backgroundColor != Color.Transparent && backgroundColor.luminance() < 0.5f) {
                        Color.White
                    } else {
                        Color.Black
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun PoopEntryItemPreview() {
    PoopEntryItem(
        entry = PoopEntry(
            date = "27/10/2025",
            hour = "12:00",
            quality = "Good",
            person = Person.FAB
        )
    )
}
