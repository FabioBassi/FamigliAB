package com.fabiobassi.famigliab.file

import android.content.Context
import com.fabiobassi.famigliab.data.CsvData
import com.fabiobassi.famigliab.data.readCsv
import com.fabiobassi.famigliab.data.writeToCsv
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class CsvFileType(val path: String) {
    PAYMENTS("Payments"),
    INCOMES("Incomes"),
    VOUCHERS("Vouchers"),
    POOP_ENTRIES("PoopTracker"),
    MEDICATIONS("Medications"),
    MEDICATION_SCHEDULES("MedicationSchedules"),
    CHR_USAGE("Miscellaneous")
}

class CsvFileManager(private val context: Context) {

    fun getFileForMonth(type: CsvFileType, date: Date): File {
        val fileName = when (type) {
            CsvFileType.POOP_ENTRIES -> "poop_entries.csv"
            CsvFileType.MEDICATIONS -> "medications.csv"
            CsvFileType.MEDICATION_SCHEDULES -> "medication_schedules.csv"
            CsvFileType.CHR_USAGE -> "chr_usage.csv"
            else -> {
                val monthFormat = SimpleDateFormat("MMM_yy", Locale.US)
                "${monthFormat.format(date).lowercase()}.csv"
            }
        }

        val baseDir = context.getExternalFilesDir(null) ?: context.filesDir

        val directoryPath = when (type) {
            CsvFileType.POOP_ENTRIES -> "FamigliAB/PoopTracker"
            CsvFileType.MEDICATIONS -> "FamigliAB/Medications"
            CsvFileType.MEDICATION_SCHEDULES -> "FamigliAB/Medications"
            CsvFileType.CHR_USAGE -> "FamigliAB/Miscellaneous"
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

    /**
     * Formats a CSV file for sharing by:
     * 1. Removing the first column if specified.
     * 2. Replacing decimal dots with commas for numeric cells.
     * 3. Using ';' as a delimiter.
     *
     * Returns a new temporary file with the formatted data.
     */
    fun formatCsvForSharing(file: File, removeFirstColumn: Boolean = false): File {
        if (!file.exists()) return file

        val sharedDir = File(context.getExternalFilesDir(null), "FamigliAB/Shared")
        if (!sharedDir.exists()) sharedDir.mkdirs()

        // Clear previous shared files to avoid confusion
        sharedDir.listFiles()?.forEach { it.delete() }

        val tempFile = File(sharedDir, "export_${file.name}")
        return try {
            val rows = csvReader().readAll(file)
            val formattedRows = rows.map { row ->
                val processedRow = if (removeFirstColumn && row.isNotEmpty()) row.drop(1) else row
                processedRow.map { cell ->
                    // Check if the cell content is a numeric value to replace dot with comma
                    if (cell.toDoubleOrNull() != null) {
                        cell.replace(".", ",")
                    } else {
                        cell
                    }
                }
            }
            csvWriter {
                delimiter = ';'
            }.writeAll(formattedRows, tempFile)
            tempFile
        } catch (e: Exception) {
            // Fallback to original file only if transformation fails
            file
        }
    }
}
