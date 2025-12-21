package com.fabiobassi.famigliab.file

import android.content.Context
import com.fabiobassi.famigliab.data.CsvData
import com.fabiobassi.famigliab.data.readCsv
import com.fabiobassi.famigliab.data.writeToCsv
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class CsvFileType(val path: String) {
    PAYMENTS("Payments"),
    INCOMES("Incomes"),
    VOUCHERS("Vouchers"),
    POOP_ENTRIES("PoopTracker")
}

class CsvFileManager(private val context: Context) {

    fun getFileForMonth(type: CsvFileType, date: Date): File {
        val fileName = when (type) {
            CsvFileType.POOP_ENTRIES -> "poop_entries.csv"
            else -> {
                val monthFormat = SimpleDateFormat("MMM_yy", Locale.US)
                "${monthFormat.format(date).lowercase()}.csv"
            }
        }

        val baseDir = context.getExternalFilesDir(null) ?: context.filesDir

        val directoryPath = when (type) {
            CsvFileType.POOP_ENTRIES -> "FamigliAB/PoopTracker"
            else -> "FamigliAB/Budgeting/${type.path}"
        }
        val directory = File(baseDir, directoryPath)

        if (!directory.exists()) {
            directory.mkdirs()
        }
        return File(directory, fileName)
    }

    fun <T : CsvData> writeData(type: CsvFileType, date: Date, data: List<T>) {
        val file = getFileForMonth(type, date)
        data.writeToCsv(file)
    }

    fun <T : CsvData> appendData(type: CsvFileType, date: Date, data: T) {
        val file = getFileForMonth(type, date)
        data.writeToCsv(file)
    }

    inline fun <reified T : CsvData> readData(
        type: CsvFileType,
        date: Date,
        noinline creator: (List<String>) -> T?
    ): List<T> {
        val file = getFileForMonth(type, date)
        return readCsv(file, creator)
    }
}