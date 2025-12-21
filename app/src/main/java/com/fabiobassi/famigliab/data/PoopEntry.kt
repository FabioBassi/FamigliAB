package com.fabiobassi.famigliab.data

import kotlinx.serialization.Serializable

@Serializable
data class PoopEntry(
    val id: Long = 0,
    val date: String,
    val hour: String,
    val quantity: String,
    val quality: String,
    val person: Person,
) : CsvData {
    override fun toCsvRow(): List<String> = listOf(
        date,
        hour,
        quantity,
        quality,
        person.name
    )

    companion object {
        fun fromCsvRow(row: List<String>): PoopEntry? {
            if (row.size < 5) return null
            return try {
                PoopEntry(
                    date = row[0],
                    hour = row[1],
                    quantity = row[2],
                    quality = row[3],
                    person = Person.valueOf(row[4])
                )
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}
