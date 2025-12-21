package com.fabiobassi.famigliab.ui.features.poop_tracker

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.PoopEntry
import com.fabiobassi.famigliab.data.SettingsDataStore
import com.fabiobassi.famigliab.file.CsvFileManager
import com.fabiobassi.famigliab.file.CsvFileType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class PoopChartData(
    val entriesByDay: Map<Person, Map<String, Int>>,
    val fabColor: Color,
    val sabColor: Color
)

class PoopTrackerViewModel(
    private val csvFileManager: CsvFileManager,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _poopEntries = MutableStateFlow<List<PoopEntry>>(emptyList())
    val poopEntries: StateFlow<List<PoopEntry>> = _poopEntries.asStateFlow()

    private val _poopChartData = MutableStateFlow<PoopChartData?>(null)
    val poopChartData: StateFlow<PoopChartData?> = _poopChartData.asStateFlow()

    private var currentDayOffset: Int = 0

    fun loadPoopEntries(dayOffset: Int = currentDayOffset) {
        currentDayOffset = dayOffset
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
            val windowSize = 7

            for (i in (dayOffset + windowSize - 1) downTo dayOffset) {
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

            val fabColorHex = settingsDataStore.getColorFor(Person.FAB.name).first()
            val sabColorHex = settingsDataStore.getColorFor(Person.SAB.name).first()

            val fabColor = if (fabColorHex.isNotEmpty()) Color(android.graphics.Color.parseColor(fabColorHex)) else Color.Blue
            val sabColor = if (sabColorHex.isNotEmpty()) Color(android.graphics.Color.parseColor(sabColorHex)) else Color.Red

            _poopChartData.value = PoopChartData(result, fabColor, sabColor)
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
                    CsvFileManager(application.applicationContext),
                    SettingsDataStore(application.applicationContext)
                ) as T
            }
        }
    }
}
