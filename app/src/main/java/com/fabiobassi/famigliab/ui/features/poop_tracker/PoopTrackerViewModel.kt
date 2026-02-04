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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

data class PersonColors(
    val fabColor: Color,
    val sabColor: Color
)

data class MonthlyStats(
    val fabCount: Int = 0,
    val fabAvg: Double = 0.0,
    val sabCount: Int = 0,
    val sabAvg: Double = 0.0
)

class PoopTrackerViewModel(
    private val csvFileManager: CsvFileManager,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _poopEntries = MutableStateFlow<List<PoopEntry>>(emptyList())
    val poopEntries: StateFlow<List<PoopEntry>> = _poopEntries.asStateFlow()

    private val _personColors = MutableStateFlow<PersonColors?>(null)
    val personColors: StateFlow<PersonColors?> = _personColors.asStateFlow()

    private val _selectedYear = MutableStateFlow(LocalDate.now().year)
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _displayedMonth = MutableStateFlow(YearMonth.now())
    val displayedMonth: StateFlow<YearMonth> = _displayedMonth.asStateFlow()

    val filteredEntries: StateFlow<List<PoopEntry>> = combine(_poopEntries, _displayedMonth) { entries, month ->
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US)
        entries.filter {
            try {
                val entryDate = LocalDate.parse(it.date, formatter)
                entryDate.monthValue == month.monthValue && entryDate.year == month.year
            } catch (_: Exception) {
                false
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recapData: StateFlow<List<Pair<Month, MonthlyStats>>> = combine(_poopEntries, _selectedYear) { entries, year ->
        calculateRecapData(entries, year)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = calculateRecapData(emptyList(), LocalDate.now().year)
    )

    init {
        loadPoopEntries()
    }

    fun updateSelectedYear(year: Int) {
        _selectedYear.value = year
    }

    fun updateDisplayedMonth(month: YearMonth) {
        _displayedMonth.value = month
    }

    private fun calculateRecapData(entries: List<PoopEntry>, year: Int): List<Pair<Month, MonthlyStats>> {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US)
        val statsMap = mutableMapOf<Month, MonthlyStats>()

        // Initialize all months
        Month.entries.forEach { month ->
            statsMap[month] = MonthlyStats()
        }

        val yearEntries = entries.filter {
            try {
                LocalDate.parse(it.date, formatter).year == year
            } catch (_: Exception) {
                false
            }
        }

        yearEntries.groupBy { LocalDate.parse(it.date, formatter).month }.forEach { (month, monthEntries) ->
            val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
            
            val fabCount = monthEntries.count { it.person == Person.FAB }
            val sabCount = monthEntries.count { it.person == Person.SAB }
            
            statsMap[month] = MonthlyStats(
                fabCount = fabCount,
                fabAvg = fabCount.toDouble() / daysInMonth,
                sabCount = sabCount,
                sabAvg = sabCount.toDouble() / daysInMonth
            )
        }

        return statsMap.toList().sortedBy { it.first.value }
    }

    fun loadPoopEntries() {
        viewModelScope.launch {
            val allEntries = csvFileManager.readData(
                type = CsvFileType.POOP_ENTRIES,
                date = Date(),
                creator = PoopEntry.Companion::fromCsvRow
            )
            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
            _poopEntries.value = allEntries.sortedByDescending {
                try {
                    dateTimeFormat.parse("${it.date} ${it.hour}")
                } catch (e: Exception) {
                    Date(0)
                }
            }

            val fabColorHex = settingsDataStore.getColorFor(Person.FAB.name).first()
            val sabColorHex = settingsDataStore.getColorFor(Person.SAB.name).first()

            val fabColor = if (fabColorHex.isNotEmpty()) Color(fabColorHex.toColorInt()) else Color.Blue
            val sabColor = if (sabColorHex.isNotEmpty()) Color(sabColorHex.toColorInt()) else Color.Red

            _personColors.value = PersonColors(fabColor, sabColor)
        }
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
            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
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
            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
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
