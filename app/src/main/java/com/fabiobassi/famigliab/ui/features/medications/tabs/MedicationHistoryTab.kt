package com.fabiobassi.famigliab.ui.features.medications.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiobassi.famigliab.data.MedicationEntry
import com.fabiobassi.famigliab.ui.features.medications.MedicationsViewModel
import com.fabiobassi.famigliab.ui.features.medications.RecordItem
import com.fabiobassi.famigliab.ui.features.medications.components.MedicationCalendar
import com.fabiobassi.famigliab.ui.features.medications.items.MedicationRecordItem
import com.fabiobassi.famigliab.ui.features.medications.items.SkippedMedicationItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MedicationHistoryTab(
    recordItems: List<RecordItem>,
    onDeleteMedication: (MedicationEntry) -> Unit,
    onMarkSkippedAsTaken: (RecordItem.Skipped, String) -> Unit,
    viewModel: MedicationsViewModel = viewModel(factory = MedicationsViewModel.Factory)
) {
    var selectedDate by remember { mutableStateOf<Date?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        MedicationCalendar(
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it },
            getDayStatus = { viewModel.getDayStatus(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedDate != null) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val dateStr = dateFormat.format(selectedDate!!)
            val filteredItems = recordItems.filter { it.date == dateStr }

            Text(
                text = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(selectedDate!!),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
            )

            if (filteredItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No records for this day",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredItems) { item ->
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
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Select a day to see history",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
