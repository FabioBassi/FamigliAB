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

sealed class HistoryItem {
    abstract val date: String
    abstract val hour: String
    abstract val name: String
    abstract val dosage: String
    abstract val person: Person
    abstract val pillsPerDose: Int

    data class Taken(val entry: MedicationEntry) : HistoryItem() {
        override val date: String = entry.date
        override val hour: String = entry.hour
        override val name: String = entry.name
        override val dosage: String = entry.dosage
        override val person: Person = entry.person
        override val pillsPerDose: Int = entry.pillsPerDose
    }

    data class Skipped(
        override val date: String,
        override val hour: String,
        override val name: String,
        override val dosage: String,
        override val person: Person,
        override val pillsPerDose: Int,
        val scheduleId: String
    ) : HistoryItem()

    val dateTime: Date
        get() = try {
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse("$date $hour") ?: Date(0)
        } catch (e: Exception) {
            Date(0)
        }
}

class MedicationsViewModel(
    private val csvFileManager: CsvFileManager
) : ViewModel() {

    private val _medicationEntries = MutableStateFlow<List<MedicationEntry>>(emptyList())
    val medicationEntries: StateFlow<List<MedicationEntry>> = _medicationEntries.asStateFlow()

    private val _medicationSchedules = MutableStateFlow<List<MedicationSchedule>>(emptyList())
    // Only show non-archived schedules in the schedule list
    val medicationSchedules: StateFlow<List<MedicationSchedule>> = combine(_medicationSchedules) { schedules ->
        schedules[0].filter { !it.isArchived }
    }.collectAsStateFlow(emptyList())

    val reminders: StateFlow<List<MedicationReminder>> = combine(
        _medicationSchedules,
        _medicationEntries
    ) { schedules, entries ->
        val calendar = Calendar.getInstance()
        val todayDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
        val todayDayOfWeek = getDayOfWeek(calendar)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val today = sdf.parse(todayDate) ?: Date()

        // Only show reminders for active (non-archived) schedules
        schedules.filter { !it.isArchived && isScheduleActiveOnDate(it, today, todayDayOfWeek) }
            .map { schedule ->
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

    val historyItems: StateFlow<List<HistoryItem>> = combine(
        _medicationSchedules,
        _medicationEntries
    ) { schedules, entries ->
        val history = mutableListOf<HistoryItem>()
        
        // Add actual entries (Taken) - even if the schedule is archived
        history.addAll(entries.map { HistoryItem.Taken(it) })

        // Calculate skipped entries for the last 14 days
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val todayCalendar = Calendar.getInstance()
        val todayDateStr = dateFormat.format(todayCalendar.time)

        for (i in 1..14) {
            val checkCalendar = Calendar.getInstance()
            checkCalendar.add(Calendar.DAY_OF_YEAR, -i)
            val checkDateStr = dateFormat.format(checkCalendar.time)
            val checkDate = dateFormat.parse(checkDateStr) ?: continue
            val dayOfWeek = getDayOfWeek(checkCalendar)

            // Only calculate skipped for active (non-archived) schedules
            schedules.filter { !it.isArchived }.forEach { schedule ->
                if (isScheduleActiveOnDate(schedule, checkDate, dayOfWeek)) {
                    val wasTaken = entries.any { it.scheduleId == schedule.id && it.date == checkDateStr }
                    if (!wasTaken) {
                        history.add(
                            HistoryItem.Skipped(
                                date = checkDateStr,
                                hour = schedule.hour,
                                name = schedule.name,
                                dosage = schedule.dosage,
                                person = schedule.person,
                                pillsPerDose = schedule.pillsPerDose,
                                scheduleId = schedule.id
                            )
                        )
                    }
                }
            }
        }

        history.sortedByDescending { it.dateTime }
    }.collectAsStateFlow(emptyList())

    private fun getDayOfWeek(calendar: Calendar): String {
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "MON"
            Calendar.TUESDAY -> "TUE"
            Calendar.WEDNESDAY -> "WED"
            Calendar.THURSDAY -> "THU"
            Calendar.FRIDAY -> "FRI"
            Calendar.SATURDAY -> "SAT"
            Calendar.SUNDAY -> "SUN"
            else -> ""
        }
    }

    private fun isScheduleActiveOnDate(schedule: MedicationSchedule, date: Date, dayOfWeek: String): Boolean {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return when (schedule.frequencyType) {
            FrequencyType.WEEKLY -> {
                schedule.daysOfWeek?.contains(dayOfWeek) == true
            }
            FrequencyType.INTERVAL -> {
                if (schedule.startDate != null && schedule.intervalDays != null) {
                    try {
                        val startDate = sdf.parse(schedule.startDate)
                        if (startDate != null && !date.before(startDate)) {
                            val diffInMillis = date.time - startDate.time
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
    }

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
            // Soft delete: mark as archived instead of removing
            val updatedSchedules = _medicationSchedules.value.map {
                if (it.id == schedule.id) it.copy(isArchived = true) else it
            }
            csvFileManager.writeData(CsvFileType.MEDICATION_SCHEDULES, Date(), updatedSchedules)
            loadData()
        }
    }

    fun markAsTaken(reminder: MedicationReminder, hour: String? = null) {
        viewModelScope.launch {
            val now = Date()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            
            val newEntry = MedicationEntry(
                date = dateFormat.format(now),
                hour = hour ?: hourFormat.format(now),
                name = reminder.schedule.name,
                dosage = reminder.schedule.dosage,
                person = reminder.schedule.person,
                scheduleId = reminder.schedule.id,
                pillsPerDose = reminder.schedule.pillsPerDose
            )
            csvFileManager.appendData(CsvFileType.MEDICATIONS, Date(), newEntry)
            loadData()
        }
    }

    fun markSkippedAsTaken(item: HistoryItem.Skipped, hour: String) {
        viewModelScope.launch {
            val newEntry = MedicationEntry(
                date = item.date,
                hour = hour,
                name = item.name,
                dosage = item.dosage,
                person = item.person,
                scheduleId = item.scheduleId,
                pillsPerDose = item.pillsPerDose
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

    fun deleteArchivedSchedulesAndHistory() {
        viewModelScope.launch {
            val archivedIds = _medicationSchedules.value.filter { it.isArchived }.map { it.id }.toSet()
            
            // Remove archived schedules
            val updatedSchedules = _medicationSchedules.value.filter { !it.isArchived }
            csvFileManager.writeData(CsvFileType.MEDICATION_SCHEDULES, Date(), updatedSchedules)
            
            // Remove entries associated with archived schedules
            val updatedEntries = _medicationEntries.value.filter { it.scheduleId !in archivedIds }
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
