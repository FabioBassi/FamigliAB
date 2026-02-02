package com.fabiobassi.famigliab.ui.features.medications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.data.FrequencyType
import com.fabiobassi.famigliab.data.MedicationSchedule

@Composable
fun ScheduleItem(
    schedule: MedicationSchedule,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
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
                    text = schedule.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${schedule.dosage} • ${schedule.person.name}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                val frequencyText = when (schedule.frequencyType) {
                    FrequencyType.WEEKLY -> "Every ${schedule.daysOfWeek}"
                    FrequencyType.INTERVAL -> "Every ${schedule.intervalDays} days (starting ${schedule.startDate})"
                }
                
                Text(
                    text = "$frequencyText at ${schedule.hour}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
