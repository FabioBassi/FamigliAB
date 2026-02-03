package com.fabiobassi.famigliab.ui.features.medications.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.data.FrequencyType
import com.fabiobassi.famigliab.data.MedicationSchedule
import com.fabiobassi.famigliab.data.Person
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String, Person, FrequencyType, Set<String>?, Int?, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var pillsPerDose by remember { mutableStateOf("1") }
    var person by remember { mutableStateOf(Person.FAB) }
    var frequencyType by remember { mutableStateOf(FrequencyType.WEEKLY) }
    var expanded by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val timePickerState = rememberTimePickerState(is24Hour = true)
    var selectedTime by remember { mutableStateOf("08:00") }

    val localizedDays = stringArrayResource(R.array.days_of_week).toList()
    val stableKeys = MedicationSchedule.DAY_KEYS
    var selectedDays by remember { mutableStateOf(stableKeys.toSet()) }
    
    var intervalDays by remember { mutableStateOf("1") }
    val datePickerState = rememberDatePickerState()
    var startDate by remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())) }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = String.format(locale = Locale.ENGLISH, "%02d:%02d", timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        startDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.add_medication)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(id = R.string.medication_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text(stringResource(id = R.string.dosage)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = pillsPerDose,
                        onValueChange = { if (it.all { char -> char.isDigit() }) pillsPerDose = it },
                        label = { Text(stringResource(R.string.pills)) },
                        modifier = Modifier.weight(0.5f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = selectedTime,
                        onValueChange = {},
                        label = { Text(stringResource(id = R.string.time)) },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            TextButton(onClick = { showTimePicker = true }) {
                                Text(stringResource(id = R.string.set))
                            }
                        }
                    )
                }

                Text(text = stringResource(R.string.frequency)+":", style = MaterialTheme.typography.labelLarge)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    FrequencyType.entries.forEachIndexed { index, type ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = FrequencyType.entries.size),
                            onClick = { frequencyType = type },
                            selected = frequencyType == type
                        ) {
                            Text(type.name)
                        }
                    }
                }

                if (frequencyType == FrequencyType.WEEKLY) {
                    Text(text = stringResource(R.string.repeat_on)+":", style = MaterialTheme.typography.labelLarge)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(localizedDays) { index, day ->
                            val stableKey = stableKeys[index]
                            FilterChip(
                                selected = selectedDays.contains(stableKey),
                                onClick = {
                                    selectedDays = if (selectedDays.contains(stableKey)) {
                                        selectedDays - stableKey
                                    } else {
                                        selectedDays + stableKey
                                    }
                                },
                                label = { Text(day) }
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = intervalDays,
                            onValueChange = { if (it.all { char -> char.isDigit() }) intervalDays = it },
                            label = { Text(stringResource(R.string.every_x_days)) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = {},
                            label = { Text(stringResource(R.string.start_date)) },
                            readOnly = true,
                            modifier = Modifier.weight(1f),
                            trailingIcon = {
                                TextButton(onClick = { showDatePicker = true }) {
                                    Text(stringResource(R.string.set))
                                }
                            }
                        )
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = person.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.person)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        Person.entries.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.name) },
                                onClick = {
                                    person = p
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    onConfirm(
                        name, 
                        dosage, 
                        pillsPerDose.toIntOrNull() ?: 1,
                        selectedTime, 
                        person, 
                        frequencyType,
                        if (frequencyType == FrequencyType.WEEKLY) selectedDays else null,
                        if (frequencyType == FrequencyType.INTERVAL) intervalDays.toIntOrNull() else null,
                        if (frequencyType == FrequencyType.INTERVAL) startDate else null
                    ) 
                },
                enabled = name.isNotBlank() && dosage.isNotBlank() && (
                    (frequencyType == FrequencyType.WEEKLY && selectedDays.isNotEmpty()) ||
                    (frequencyType == FrequencyType.INTERVAL && intervalDays.toIntOrNull() != null)
                )
            ) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}
