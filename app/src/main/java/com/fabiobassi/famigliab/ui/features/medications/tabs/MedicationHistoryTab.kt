package com.fabiobassi.famigliab.ui.features.medications.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.MedicationEntry
import com.fabiobassi.famigliab.ui.features.medications.RecordItem
import com.fabiobassi.famigliab.ui.features.medications.items.MedicationRecordItem
import com.fabiobassi.famigliab.ui.features.medications.items.SkippedMedicationItem

@Composable
fun MedicationHistoryTab(
    recordItems: List<RecordItem>,
    onDeleteMedication: (MedicationEntry) -> Unit,
    onMarkSkippedAsTaken: (RecordItem.Skipped, String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(recordItems) { item ->
            when (item) {
                is RecordItem.Taken -> {
                    MedicationRecordItem(
                        entry = item.entry,
                        onDelete = { onDeleteMedication(item.entry) }
                    )
                }
                is RecordItem.Skipped -> {
                    SkippedMedicationItem(
                        item = item,
                        onMarkAsTaken = { hour -> onMarkSkippedAsTaken(item, hour) }
                    )
                }
            }
        }
    }
}
