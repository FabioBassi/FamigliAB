package com.fabiobassi.famigliab.ui.features.medications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.R

@Composable
fun ReminderItem(
    reminder: MedicationReminder,
    onMarkAsTaken: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = if (reminder.isTakenToday) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.schedule.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (reminder.isTakenToday) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${reminder.schedule.dosage} • ${reminder.schedule.person.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (reminder.isTakenToday) {
                        stringResource(id = R.string.taken_at, reminder.lastTakenTime ?: "")
                    } else {
                        "Scheduled at ${reminder.schedule.hour}"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (reminder.isTakenToday) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                )
            }
            IconButton(
                onClick = onMarkAsTaken,
                enabled = !reminder.isTakenToday
            ) {
                Icon(
                    imageVector = if (reminder.isTakenToday) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = stringResource(id = R.string.mark_as_taken),
                    tint = if (reminder.isTakenToday) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
