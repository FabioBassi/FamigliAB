package com.fabiobassi.famigliab.data

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface CsvData {
    fun toCsvRow(): List<String>

    companion object {
        inline fun <reified T : CsvData> fromCsvRow(
            row: List<String>,
            creator: (List<String>) -> T
        ): T {
            return creator(row)
        }
    }
}

fun <T : CsvData> T.writeToCsv(file: File) {
    csvWriter().writeAll(listOf(this.toCsvRow()), file, append = true)
}

inline fun <reified T : CsvData> readCsv(
    file: File,
    noinline creator: (List<String>) -> T
): List<T> {
    if (!file.exists()) return emptyList()
    return csvReader().readAll(file).map { CsvData.fromCsvRow(it, creator) }
}