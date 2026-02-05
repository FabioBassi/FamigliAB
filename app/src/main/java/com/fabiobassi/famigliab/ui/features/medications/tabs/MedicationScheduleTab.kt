package com.fabiobassi.famigliab.ui.features.medications.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.MedicationSchedule
import com.fabiobassi.famigliab.ui.features.medications.items.MedicationScheduleItem

@Composable
fun MedicationScheduleTab(
    medicationSchedules: List<MedicationSchedule>,
    onDeleteSchedule: (MedicationSchedule) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(medicationSchedules) { schedule ->
            MedicationScheduleItem(
                schedule = schedule,
                onDelete = { onDeleteSchedule(schedule) }
            )
        }
    }
}
