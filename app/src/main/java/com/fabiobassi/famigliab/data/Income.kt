package com.fabiobassi.famigliab.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Income(
    val description: String,
    val amount: Double,
    val paidTo: Person,
) : CsvData {

    override fun toCsvRow(): List<String> = listOf(
        description,
        amount.toString(),
        paidTo.name
    )

    companion object {
        fun fromCsvRow(row: List<String>): Income {
            return Income(
                description = row[0],
                amount = row[1].toDouble(),
                paidTo = Person.valueOf(row[2])
            )
        }
    }
}