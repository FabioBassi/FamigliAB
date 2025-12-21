package com.fabiobassi.famigliab.data

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

interface CsvData {
    fun toCsvRow(): List<String>

    companion object {
        inline fun <reified T : CsvData> fromCsvRow(
            row: List<String>,
            creator: (List<String>) -> T?
        ): T? {
            return creator(row)
        }
    }
}

fun <T : CsvData> T.writeToCsv(file: File) {
    csvWriter().writeAll(listOf(this.toCsvRow()), file, append = true)
}

fun <T : CsvData> List<T>.writeToCsv(file: File) {
    val rows = this.map { it.toCsvRow() }
    csvWriter().writeAll(rows, file, append = false)
}

inline fun <reified T : CsvData> readCsv(
    file: File,
    noinline creator: (List<String>) -> T?
): List<T> {
    if (!file.exists() || file.length() == 0L) return emptyList()
    return csvReader().readAll(file)
        .filter { it.any(String::isNotEmpty) }
        .mapNotNull { CsvData.fromCsvRow(it, creator) }
}