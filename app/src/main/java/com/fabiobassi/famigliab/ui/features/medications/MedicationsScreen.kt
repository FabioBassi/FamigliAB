package com.fabiobassi.famigliab.ui.features.medications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiobassi.famigliab.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationsScreen(
    paddingValues: PaddingValues,
    viewModel: MedicationsViewModel = viewModel(factory = MedicationsViewModel.Factory)
) {
    val medicationEntries by viewModel.medicationEntries.collectAsState()
    val medicationSchedules by viewModel.medicationSchedules.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        R.string.today,
        R.string.schedule,
        R.string.history
    )

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tabs.forEachIndexed { index, labelRes ->
                        val selected = selectedTab == index
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            onClick = { selectedTab = index },
                            shape = when (index) {
                                0 -> RoundedCornerShape(
                                    topStart = 24.dp,
                                    bottomStart = 24.dp,
                                    topEnd = 8.dp,
                                    bottomEnd = 8.dp
                                )
                                1 -> RoundedCornerShape(
                                    topStart = 8.dp,
                                    bottomStart = 8.dp,
                                    topEnd = 8.dp,
                                    bottomEnd = 8.dp
                                )
                                2 -> RoundedCornerShape(
                                    topStart = 8.dp,
                                    bottomStart = 8.dp,
                                    topEnd = 24.dp,
                                    bottomEnd = 24.dp
                                )
                                else -> RoundedCornerShape(0.dp)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                                contentColor = if (selected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp
                            )
                        ) {
                            Text(
                                text = stringResource(id = labelRes),
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_medication))
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(reminders) { reminder ->
                            ReminderItem(
                                reminder = reminder,
                                onMarkAsTaken = { viewModel.markAsTaken(reminder) }
                            )
                        }
                    }
                }
                1 -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(medicationSchedules) { schedule ->
                            ScheduleItem(
                                schedule = schedule,
                                onDelete = { viewModel.deleteSchedule(schedule) }
                            )
                        }
                    }
                }
                2 -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(medicationEntries) { entry ->
                            MedicationItem(
                                entry = entry,
                                onDelete = { viewModel.deleteMedication(entry) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddMedicationDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, dosage, pills, hour, person, frequencyType, days, interval, start ->
                viewModel.addSchedule(name, dosage, pills, hour, person, frequencyType, days, interval, start)
                showDialog = false
            }
        )
    }
}
