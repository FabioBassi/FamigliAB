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
    VOUCHERS("Vouchers")
}

class CsvFileManager(private val context: Context) {

    fun getFileForMonth(type: CsvFileType, date: Date): File {
        val monthFormat = SimpleDateFormat("MMM_yy", Locale.getDefault())
        val fileName = "${monthFormat.format(date).lowercase()}.csv"
        val subfolder = type.path
        val baseDir = File(context.getExternalFilesDir(null), "FamigliAB/Budgeting")
        val typeDir = File(baseDir, subfolder)
        if (!typeDir.exists()) {
            typeDir.mkdirs()
        }
        return File(typeDir, fileName)
    }

    fun <T : CsvData> writeData(type: CsvFileType, date: Date, data: List<T>) {
        val file = getFileForMonth(type, date)
        data.writeToCsv(file)
    }

    // FIX APPLIED HERE:
    // 1. The function is marked as 'inline'.
    // 2. The generic type 'T' is marked as 'reified'.
    inline fun <reified T : CsvData> readData(
        type: CsvFileType,
        date: Date,
        noinline creator: (List<String>) -> T
    ): List<T> {
        val file = getFileForMonth(type, date)
        // Since T is now reified, readCsv can use it.
        // Assuming readCsv is also an inline function with a reified type parameter.
        return readCsv(file, creator)
    }
}
