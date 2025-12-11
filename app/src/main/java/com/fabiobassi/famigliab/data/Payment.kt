package com.fabiobassi.famigliab.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Payment(
    val date: Date,
    val description: String,
    val amount: Double,
    val paidBy: Person,
    val category: Category
) : CsvData {

    override fun toCsvRow(): List<String> = listOf(
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date),
        description,
        amount.toString(),
        paidBy.name,
        category.name
    )

    companion object {
        fun fromCsvRow(row: List<String>): Payment {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return Payment(
                date = dateFormat.parse(row[0]) ?: Date(),
                description = row[1],
                amount = row[2].toDouble(),
                paidBy = Person.valueOf(row[3]),
                category = Category.valueOf(row[4])
            )
        }
    }
}