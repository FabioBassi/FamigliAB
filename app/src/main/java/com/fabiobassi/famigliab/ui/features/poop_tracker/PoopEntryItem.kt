package com.fabiobassi.famigliab.ui.features.poop_tracker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.PoopEntry
import com.fabiobassi.famigliab.data.SettingsDataStore
import com.fabiobassi.famigliab.ui.features.poop_tracker.dialogs.DeleteConfirmationDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PoopEntryItem(
    entry: PoopEntry,
    onDelete: (PoopEntry) -> Unit,
) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val personColorHex by settingsDataStore.getColorFor(entry.person.name)
        .collectAsState(initial = "")
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                onDelete(entry)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    val personColor = if (personColorHex.isNotEmpty()) {
        Color(personColorHex.toColorInt())
    } else {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = { /* No-op */ },
                onLongClick = { showDialog = true }
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Quality Icon
            Icon(
                imageVector = if (entry.quality == "Good") Icons.Default.SentimentSatisfiedAlt else Icons.Default.SentimentDissatisfied,
                contentDescription = null,
                tint = if (entry.quality == "Good") Color(0xFF4CAF50) else Color(0xFFF44336),
                modifier = Modifier.size(28.dp)
            )

            // Date and Time
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.date,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = entry.hour,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Person Badge
            Text(
                text = entry.person.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = personColor,
                modifier = Modifier
                    .background(personColor.copy(alpha = 0.15f), MaterialTheme.shapes.extraSmall)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PoopEntryItemPreview() {
    PoopEntryItem(
        entry = PoopEntry(
            date = "27/10/2025",
            hour = "12:00",
            quality = "Good",
            person = Person.FAB
        ),
        onDelete = {}
    )
}
