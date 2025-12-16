package com.fabiobassi.famigliab.data

import androidx.compose.ui.text.toUpperCase
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
        fun fromCsvRow(row: List<String>): Payment? {
            val regex1 = Regex("""^\d{4}-\d{2}-\d{2}$""")
            val regex2 = Regex("""^\d{2}/\d{2}/\d{2}$""")
            val dateFormat = if (row.size == 6 && regex1.matches(row[1]))
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            else if (row.size == 5 && regex2.matches(row[0]))
                SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            else
                SimpleDateFormat()
            var p: Payment
            try {
                when (row.size) {
                    6 -> {
                        p = Payment(
                            id = row[0],
                            date = dateFormat.parse(row[1]) ?: Date(),
                            description = row[2],
                            amount = row[3].replace(",", ".").toDouble(),
                            paidBy = Person.valueOf(row[4]),
                            category = Category.valueOf(row[5].uppercase(Locale.getDefault()))
                        )
                    }
                    5 -> {
                        p = Payment(
                            date = dateFormat.parse(row[0]) ?: Date(),
                            description = row[1],
                            amount = row[2].replace(",", ".").toDouble(),
                            paidBy = Person.valueOf(row[3]),
                            category = Category.valueOf(row[4].uppercase(Locale.getDefault()))
                        )
                    }
                    else -> {
                        throw IllegalArgumentException("Invalid number of columns in CSV row")
                    }
                }
                return p
            } catch (e: Exception) {
                null
            }
            return null
        }
    }
}