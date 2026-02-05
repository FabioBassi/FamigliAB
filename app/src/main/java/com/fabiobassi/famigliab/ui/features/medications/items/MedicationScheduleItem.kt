package com.fabiobassi.famigliab.ui.features.medications.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.data.FrequencyType
import com.fabiobassi.famigliab.data.MedicationSchedule
import com.fabiobassi.famigliab.data.SettingsDataStore

@Composable
fun MedicationScheduleItem(
    schedule: MedicationSchedule,
    onDelete: () -> Unit,
) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val personColorHex by settingsDataStore.getColorFor(schedule.person.name)
        .collectAsState(initial = "")
    val personColor = if (personColorHex.isNotEmpty()) {
        Color(personColorHex.toColorInt())
    } else {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = schedule.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Person Badge
                    Surface(
                        color = personColor.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = schedule.person.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = personColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = pluralStringResource(
                            id = R.plurals.pills_to_take,
                            count = schedule.pillsPerDose,
                            schedule.pillsPerDose
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.medication_dosage, schedule.dosage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val localizedDays = stringArrayResource(R.array.days_of_week)
                val stableKeys = MedicationSchedule.DAY_KEYS
                
                val frequencyText = when (schedule.frequencyType) {
                    FrequencyType.WEEKLY -> {
                        val scheduleDays = schedule.daysOfWeek?.split(",") ?: emptyList()
                        val localizedScheduleDays = scheduleDays.mapNotNull { key ->
                            val index = stableKeys.indexOf(key)
                            if (index != -1) localizedDays[index] else null
                        }.joinToString(", ")
                        stringResource(R.string.frequency_weekly, localizedScheduleDays)
                    }
                    FrequencyType.INTERVAL -> pluralStringResource(
                        id = R.plurals.frequency_interval,
                        count = schedule.intervalDays ?: 0,
                        schedule.intervalDays ?: 0,
                        schedule.startDate ?: ""
                    )
                }

                Text(
                    text = stringResource(R.string.frequency_at_time, frequencyText, schedule.hour),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 8.dp)
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
