package com.fabiobassi.famigliab.ui.features.poop_tracker

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
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

data class CumulativePoopChartData(
    val entries: Map<Person, List<Pair<String, Int>>>,
    val fabColor: Color,
    val sabColor: Color,
    val year: Int,
)

class PoopTrackerViewModel(
    private val csvFileManager: CsvFileManager,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _poopEntries = MutableStateFlow<List<PoopEntry>>(emptyList())
    val poopEntries: StateFlow<List<PoopEntry>> = _poopEntries.asStateFlow()

    private val _poopChartData = MutableStateFlow<PoopChartData?>(null)
    val poopChartData: StateFlow<PoopChartData?> = _poopChartData.asStateFlow()

    private val _cumulativePoopChartData = MutableStateFlow<CumulativePoopChartData?>(null)
    val cumulativePoopChartData: StateFlow<CumulativePoopChartData?> =
        _cumulativePoopChartData.asStateFlow()

    private var currentDayOffset: Int = 0
    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    fun loadPoopEntries(dayOffset: Int = currentDayOffset, year: Int = _selectedYear.value) {
        currentDayOffset = dayOffset
        _selectedYear.value = year
        viewModelScope.launch {
            val allEntries = csvFileManager.readData(
                type = CsvFileType.POOP_ENTRIES,
                date = Date(),
                creator = PoopEntry.Companion::fromCsvRow
            )
            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            _poopEntries.value = allEntries.sortedByDescending {
                try {
                    dateTimeFormat.parse("${it.date} ${it.hour}")
                } catch (e: Exception) {
                    Date(0)
                }
            }

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

            for (person in Person.entries) {
                val personEntries = entriesByPerson[person] ?: emptyList()
                val personMap = lastDays.toMutableMap()
                personEntries.forEach { entry ->
                    val entryDate =
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(entry.date)
                    val entryDay = dateFormat.format(entryDate)
                    if (personMap.containsKey(entryDay)) {
                        personMap[entryDay] = personMap[entryDay]!! + 1
                    }
                }
                result[person] = personMap
            }

            val fabColorHex = settingsDataStore.getColorFor(Person.FAB.name).first()
            val sabColorHex = settingsDataStore.getColorFor(Person.SAB.name).first()

            val fabColor = if (fabColorHex.isNotEmpty()) Color(fabColorHex.toColorInt()) else Color.Blue
            val sabColor = if (sabColorHex.isNotEmpty()) Color(sabColorHex.toColorInt()) else Color.Red

            _poopChartData.value = PoopChartData(result, fabColor, sabColor)

            val cumulativeResult = mutableMapOf<Person, List<Pair<String, Int>>>()
            val monthFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
            val dateParseFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            val allMonths = mutableListOf<String>()
            val cal = Calendar.getInstance()
            for (i in 0..11) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, i)
                allMonths.add(monthFormat.format(cal.time))
            }

            for (person in Person.entries) {
                val personEntries = entriesByPerson[person] ?: emptyList()
                val monthCounts = allMonths.associateWith { 0 }.toMutableMap()

                personEntries.forEach { entry ->
                    try {
                        val entryDate = dateParseFormat.parse(entry.date)
                        val entryCal = Calendar.getInstance()
                        entryCal.time = entryDate
                        if (entryCal.get(Calendar.YEAR) == year) {
                            val month = monthFormat.format(entryDate)
                            if (monthCounts.containsKey(month)) {
                                monthCounts[month] = monthCounts[month]!! + 1
                            }
                        }
                    } catch (e: Exception) {
                        // ignore malformed dates
                    }
                }

                var cumulativeCount = 0
                val cumulativeEntries = allMonths.map { month ->
                    cumulativeCount += monthCounts[month]!!
                    month to cumulativeCount
                }
                cumulativeResult[person] = cumulativeEntries
            }

            _cumulativePoopChartData.value = CumulativePoopChartData(
                entries = cumulativeResult,
                fabColor = fabColor,
                sabColor = sabColor,
                year = year,
            )
        }
    }

    fun changeYear(year: Int) {
        loadPoopEntries(year = year)
    }

    fun addPoopEntry(date: String, hour: String, quality: String, person: Person) {
        viewModelScope.launch {
            val newEntry = PoopEntry(
                date = date,
                hour = hour,
                quality = quality,
                person = person
            )
            val allEntries = csvFileManager.readData(
                type = CsvFileType.POOP_ENTRIES,
                date = Date(),
                creator = PoopEntry.Companion::fromCsvRow
            )
            val updatedEntries = allEntries + newEntry
            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val sortedEntries = updatedEntries.sortedByDescending {
                try {
                    dateTimeFormat.parse("${it.date} ${it.hour}")
                } catch (e: Exception) {
                    Date(0)
                }
            }
            csvFileManager.writeData(CsvFileType.POOP_ENTRIES, Date(), sortedEntries)
            loadPoopEntries()
        }
    }

    fun deletePoopEntry(entry: PoopEntry) {
        viewModelScope.launch {
            val allEntries = csvFileManager.readData(
                type = CsvFileType.POOP_ENTRIES,
                date = Date(),
                creator = PoopEntry.Companion::fromCsvRow
            )
            val updatedEntries = allEntries.filter { it != entry }
            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val sortedEntries = updatedEntries.sortedByDescending {
                try {
                    dateTimeFormat.parse("${it.date} ${it.hour}")
                } catch (e: Exception) {
                    Date(0)
                }
            }
            csvFileManager.writeData(CsvFileType.POOP_ENTRIES, Date(), sortedEntries)
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
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return PoopTrackerViewModel(
                    CsvFileManager(application.applicationContext),
                    SettingsDataStore(application.applicationContext)
                ) as T
            }
        }
    }
}