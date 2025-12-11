package com.fabiobassi.famigliab.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class Payment(
    val id: String = UUID.randomUUID().toString(),
    val date: Date,
    val description: String,
    val amount: Double,
    val paidBy: Person,
    val category: Category
) : CsvData {

    override fun toCsvRow(): List<String> = listOf(
        id,
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date),
        description,
        amount.toString(),
        paidBy.name,
        category.name
    )

    companion object {
        fun fromCsvRow(row: List<String>): Payment {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return if (row.size == 6) {
                Payment(
                    id = row[0],
                    date = dateFormat.parse(row[1]) ?: Date(),
                    description = row[2],
                    amount = row[3].toDouble(),
                    paidBy = Person.valueOf(row[4]),
                    category = Category.valueOf(row[5])
                )
            } else {
                Payment(
                    date = dateFormat.parse(row[0]) ?: Date(),
                    description = row[1],
                    amount = row[2].toDouble(),
                    paidBy = Person.valueOf(row[3]),
                    category = Category.valueOf(row[4])
                )
            }
        }
    }
}