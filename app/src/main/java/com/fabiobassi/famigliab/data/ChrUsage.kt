package com.fabiobassi.famigliab.data

import kotlinx.serialization.Serializable
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@Serializable
data class ChrUsage(
    val week: Int,
    val date: String,
    val expectedKm: Int,
    val actualKm: Int? = null,
) : CsvData {
    override fun toCsvRow(): List<String> = listOf(
        week.toString(),
        date,
        expectedKm.toString(),
        actualKm?.toString() ?: ""
    )

    companion object {
        fun fromCsvRow(row: List<String>): ChrUsage? {
            if (row.size < 4) return null
            return try {
                ChrUsage(
                    week = row[0].toInt(),
                    date = row[1],
                    expectedKm = row[2].toInt(),
                    actualKm = row[3].toIntOrNull()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

fun populateFile(file: File) {
    val entries = mutableListOf<ChrUsage>()
    val date : Date = Date.from(
        LocalDate.of(2025, 11, 7)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
    )
    val formatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    val kmPerWeek: Double = 90000.0 / 210.0
    var weekKm: Double = 0.0
    for (i in 1..211) {
        entries += ChrUsage(
            week = i,
            date = formatter.format(date),
            expectedKm = weekKm.toInt(),
            actualKm = if (i == 1) 0 else null
        )
        date.time = Date.from(
            date.toInstant().plus(7, java.time.temporal.ChronoUnit.DAYS)
        ).time
        weekKm += kmPerWeek
    }

    entries.writeToCsv(file)
}
