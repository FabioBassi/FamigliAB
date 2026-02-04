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
import java.time.YearMonth
import java.util.Date
import java.util.Locale

data class PoopData(
    val fabColor: Color,
    val sabColor: Color
)

class PoopTrackerViewModel(
    private val csvFileManager: CsvFileManager,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _poopEntries = MutableStateFlow<List<PoopEntry>>(emptyList())
    val poopEntries: StateFlow<List<PoopEntry>> = _poopEntries.asStateFlow()

    private val _poopData = MutableStateFlow<PoopData?>(null)
    val poopData: StateFlow<PoopData?> = _poopData.asStateFlow()

    fun loadPoopEntries(month: YearMonth? = null) {
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

            _poopData.value = PoopData(fabColor, sabColor)
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
