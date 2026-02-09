package com.fabiobassi.famigliab.ui.features.miscellaneus.toyota_chr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.fabiobassi.famigliab.data.ChrUsage
import com.fabiobassi.famigliab.data.populateFile
import com.fabiobassi.famigliab.file.CsvFileManager
import com.fabiobassi.famigliab.file.CsvFileType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class ChrUsageViewModel(
    private val csvFileManager: CsvFileManager
) : ViewModel() {

    private val _entries = MutableStateFlow<List<ChrUsage>>(emptyList())
    val entries: StateFlow<List<ChrUsage>> = _entries.asStateFlow()

    init {
        checkAndCreateFile()
        loadEntries()
    }

    private fun checkAndCreateFile() {
        val file = csvFileManager.getFileForMonth(CsvFileType.CHR_USAGE, Date())
        if (!file.exists()) {
            file.createNewFile()
            populateFile(file)
        }
    }

    fun loadEntries() {
        viewModelScope.launch {
            val loadedEntries = csvFileManager.readData(
                type = CsvFileType.CHR_USAGE,
                date = Date(),
                creator = ChrUsage.Companion::fromCsvRow
            )
            _entries.value = loadedEntries
        }
    }

    fun updateEntry(updatedEntry: ChrUsage) {
        viewModelScope.launch {
            val currentList = _entries.value.toMutableList()
            val index = currentList.indexOfFirst { it.week == updatedEntry.week }
            if (index != -1) {
                currentList[index] = updatedEntry
                _entries.value = currentList
                csvFileManager.writeData(
                    type = CsvFileType.CHR_USAGE,
                    date = Date(),
                    data = currentList
                )
            }
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
                return ChrUsageViewModel(
                    CsvFileManager(application.applicationContext)
                ) as T
            }
        }
    }
}
