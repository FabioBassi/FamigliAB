package com.fabiobassi.famigliab.ui.features.medications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.fabiobassi.famigliab.data.FrequencyType
import com.fabiobassi.famigliab.data.MedicationEntry
import com.fabiobassi.famigliab.data.MedicationSchedule
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.file.CsvFileManager
import com.fabiobassi.famigliab.file.CsvFileType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class MedicationReminder(
    val schedule: MedicationSchedule,
    val isTakenToday: Boolean,
    val lastTakenTime: String?
)

class MedicationsViewModel(
    private val csvFileManager: CsvFileManager
) : ViewModel() {

    private val _medicationEntries = MutableStateFlow<List<MedicationEntry>>(emptyList())
    val medicationEntries: StateFlow<List<MedicationEntry>> = _medicationEntries.asStateFlow()

    private val _medicationSchedules = MutableStateFlow<List<MedicationSchedule>>(emptyList())
    val medicationSchedules: StateFlow<List<MedicationSchedule>> = _medicationSchedules.asStateFlow()

    val reminders: StateFlow<List<MedicationReminder>> = combine(
        _medicationSchedules,
        _medicationEntries
    ) { schedules, entries ->
        val calendar = Calendar.getInstance()
        val todayDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
        val todayDayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "MON"
            Calendar.TUESDAY -> "TUE"
            Calendar.WEDNESDAY -> "WED"
            Calendar.THURSDAY -> "THU"
            Calendar.FRIDAY -> "FRI"
            Calendar.SATURDAY -> "SAT"
            Calendar.SUNDAY -> "SUN"
            else -> ""
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val today = sdf.parse(todayDate) ?: Date()

        schedules.filter { schedule ->
            when (schedule.frequencyType) {
                FrequencyType.WEEKLY -> {
                    schedule.daysOfWeek?.contains(todayDayOfWeek) == true
                }
                FrequencyType.INTERVAL -> {
                    if (schedule.startDate != null && schedule.intervalDays != null) {
                        try {
                            val startDate = sdf.parse(schedule.startDate)
                            if (startDate != null && !today.before(startDate)) {
                                val diffInMillis = today.time - startDate.time
                                val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
                                diffInDays % schedule.intervalDays == 0
                            } else {
                                false
                            }
                        } catch (e: Exception) {
                            false
                        }
                    } else {
                        false
                    }
                }
            }
        }.map { schedule ->
            val takenToday = entries.find { 
                it.scheduleId == schedule.id && it.date == todayDate 
            }
            MedicationReminder(
                schedule = schedule,
                isTakenToday = takenToday != null,
                lastTakenTime = takenToday?.hour
            )
        }.sortedBy { it.schedule.hour }
    }.collectAsStateFlow(emptyList())

    private fun <T> kotlinx.coroutines.flow.Flow<T>.collectAsStateFlow(initialValue: T): StateFlow<T> {
        val flow = this
        val state = MutableStateFlow(initialValue)
        viewModelScope.launch {
            flow.collect { state.value = it }
        }
        return state.asStateFlow()
    }

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val date = Date()
            val entries = csvFileManager.readData(
                type = CsvFileType.MEDICATIONS,
                date = date,
                creator = MedicationEntry.Companion::fromCsvRow
            )
            val schedules = csvFileManager.readData(
                type = CsvFileType.MEDICATION_SCHEDULES,
                date = date,
                creator = MedicationSchedule.Companion::fromCsvRow
            )

            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            _medicationEntries.value = entries.sortedByDescending {
                try {
                    dateTimeFormat.parse("${it.date} ${it.hour}")
                } catch (e: Exception) {
                    Date(0)
                }
            }
            _medicationSchedules.value = schedules
        }
    }

    fun addSchedule(
        name: String, 
        dosage: String, 
        pillsPerDose: Int,
        hour: String, 
        person: Person, 
        frequencyType: FrequencyType,
        daysOfWeek: Set<String>?,
        intervalDays: Int?,
        startDate: String?
    ) {
        viewModelScope.launch {
            val newSchedule = MedicationSchedule(
                name = name,
                dosage = dosage,
                pillsPerDose = pillsPerDose,
                hour = hour,
                person = person,
                frequencyType = frequencyType,
                daysOfWeek = daysOfWeek?.joinToString(","),
                intervalDays = intervalDays,
                startDate = startDate
            )
            csvFileManager.appendData(CsvFileType.MEDICATION_SCHEDULES, Date(), newSchedule)
            loadData()
        }
    }

    fun deleteSchedule(schedule: MedicationSchedule) {
        viewModelScope.launch {
            val updatedSchedules = _medicationSchedules.value.filter { it.id != schedule.id }
            csvFileManager.writeData(CsvFileType.MEDICATION_SCHEDULES, Date(), updatedSchedules)
            loadData()
        }
    }

    fun markAsTaken(reminder: MedicationReminder) {
        viewModelScope.launch {
            val now = Date()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            
            val newEntry = MedicationEntry(
                date = dateFormat.format(now),
                hour = hourFormat.format(now),
                name = reminder.schedule.name,
                dosage = reminder.schedule.dosage,
                person = reminder.schedule.person,
                scheduleId = reminder.schedule.id
            )
            csvFileManager.appendData(CsvFileType.MEDICATIONS, Date(), newEntry)
            loadData()
        }
    }

    fun deleteMedication(entry: MedicationEntry) {
        viewModelScope.launch {
            val updatedEntries = _medicationEntries.value.filter { it != entry }
            csvFileManager.writeData(CsvFileType.MEDICATIONS, Date(), updatedEntries)
            loadData()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return MedicationsViewModel(
                    CsvFileManager(application.applicationContext)
                ) as T
            }
        }
    }
}
