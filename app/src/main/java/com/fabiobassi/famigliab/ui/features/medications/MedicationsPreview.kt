package com.fabiobassi.famigliab.ui.features.medications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.FrequencyType
import com.fabiobassi.famigliab.data.MedicationEntry
import com.fabiobassi.famigliab.data.MedicationSchedule
import com.fabiobassi.famigliab.data.Person

@Preview(showBackground = true, name = "Reminder Item")
@Composable
fun ReminderItemPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            val schedule = MedicationSchedule(
                name = "Aspirin",
                dosage = "100mg",
                hour = "08:00",
                person = Person.FAB,
                frequencyType = FrequencyType.WEEKLY,
                daysOfWeek = "MON,WED,FRI"
            )
            ReminderItem(
                reminder = MedicationReminder(
                    schedule = schedule,
                    isTakenToday = false,
                    lastTakenTime = null
                ),
                onMarkAsTaken = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Reminder Item - Taken")
@Composable
fun ReminderItemTakenPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            val schedule = MedicationSchedule(
                name = "Aspirin",
                dosage = "100mg",
                hour = "08:00",
                person = Person.FAB,
                frequencyType = FrequencyType.WEEKLY,
                daysOfWeek = "MON,WED,FRI"
            )
            ReminderItem(
                reminder = MedicationReminder(
                    schedule = schedule,
                    isTakenToday = true,
                    lastTakenTime = "08:05"
                ),
                onMarkAsTaken = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Schedule Item - Weekly")
@Composable
fun ScheduleItemWeeklyPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            ScheduleItem(
                schedule = MedicationSchedule(
                    name = "Vitamin D",
                    dosage = "2000 IU",
                    hour = "09:00",
                    person = Person.SAB,
                    frequencyType = FrequencyType.WEEKLY,
                    daysOfWeek = "MON,TUE,WED,THU,FRI,SAT,SUN"
                ),
                onDelete = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Schedule Item - Interval")
@Composable
fun ScheduleItemIntervalPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            ScheduleItem(
                schedule = MedicationSchedule(
                    name = "Antibiotic",
                    dosage = "500mg",
                    hour = "22:00",
                    person = Person.FAB,
                    frequencyType = FrequencyType.INTERVAL,
                    intervalDays = 2,
                    startDate = "01/01/2024"
                ),
                onDelete = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Medication History Item")
@Composable
fun MedicationItemPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            MedicationItem(
                entry = MedicationEntry(
                    date = "24/01/2024",
                    hour = "08:15",
                    name = "Aspirin",
                    dosage = "100mg",
                    person = Person.FAB
                ),
                onDelete = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Add Medication Dialog - Weekly")
@Composable
fun AddMedicationDialogWeeklyPreview() {
    MaterialTheme {
        AddMedicationDialog(
            onDismiss = {},
            onConfirm = { _, _, _, _, _, _, _, _, _ -> }
        )
    }
}
