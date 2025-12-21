package com.fabiobassi.famigliab.ui.features.poop_tracker

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.PoopEntry
import com.fabiobassi.famigliab.file.CsvFileManager
import com.fabiobassi.famigliab.file.CsvFileType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PoopTrackerViewModel(private val csvFileManager: CsvFileManager) : ViewModel() {

    private val _poopEntries = MutableStateFlow<List<PoopEntry>>(emptyList())
    val poopEntries: StateFlow<List<PoopEntry>> = _poopEntries.asStateFlow()

    private val _poopEntriesByDay = MutableStateFlow<Map<Person, Map<String, Int>>>(emptyMap())
    val poopEntriesByDay: StateFlow<Map<Person, Map<String, Int>>> = _poopEntriesByDay.asStateFlow()

    private var currentDays: Int = 7

    fun loadPoopEntries(days: Int = currentDays) {
        currentDays = days
        viewModelScope.launch {
            val allEntries = csvFileManager.readData(
                type = CsvFileType.POOP_ENTRIES,
                date = Date(),
                creator = PoopEntry.Companion::fromCsvRow
            )
            _poopEntries.value = allEntries

            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
            val today = calendar.time
            val lastDays = mutableMapOf<String, Int>()

            for (i in (days - 1) downTo 0) {
                calendar.time = today
                calendar.add(Calendar.DAY_OF_YEAR, -i)
                val day = dateFormat.format(calendar.time)
                lastDays[day] = 0
            }

            val entriesByPerson = allEntries.groupBy { it.person }

            val result = mutableMapOf<Person, Map<String, Int>>()

            for (person in Person.values()) {
                val personEntries = entriesByPerson[person] ?: emptyList()
                val personMap = lastDays.toMutableMap()
                personEntries.forEach { entry ->
                    val entryDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(entry.date)
                    val entryDay = dateFormat.format(entryDate)
                    if (personMap.containsKey(entryDay)) {
                        personMap[entryDay] = personMap[entryDay]!! + 1
                    }
                }
                result[person] = personMap
            }

            _poopEntriesByDay.value = result
        }
    }

    fun addPoopEntry(date: String, hour: String, quantity: String, quality: String, person: Person) {
        viewModelScope.launch {
            val newEntry = PoopEntry(
                date = date,
                hour = hour,
                quantity = quantity,
                quality = quality,
                person = person
            )
            csvFileManager.appendData(CsvFileType.POOP_ENTRIES, Date(), newEntry)
            loadPoopEntries()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return PoopTrackerViewModel(
                    CsvFileManager(application.applicationContext)
                ) as T
            }
        }
    }
}
