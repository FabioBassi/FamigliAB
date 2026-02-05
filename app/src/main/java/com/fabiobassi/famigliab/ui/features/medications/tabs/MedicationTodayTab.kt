package com.fabiobassi.famigliab.ui.features.medications.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.ui.features.medications.MedicationReminder
import com.fabiobassi.famigliab.ui.features.medications.items.MedicationReminderItem

@Composable
fun MedicationTodayTab(
    reminders: List<MedicationReminder>,
    onMarkAsTaken: (MedicationReminder, String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(reminders) { reminder ->
            MedicationReminderItem(
                reminder = reminder,
                onMarkAsTaken = { hour -> onMarkAsTaken(reminder, hour) }
            )
        }
    }
}
