package com.fabiobassi.famigliab.data

import kotlinx.serialization.Serializable

@Serializable
data class PoopEntry(
    val id: Long = 0,
    val date: String,
    val hour: String,
    val quality: String,
    val person: Person,
) : CsvData {
    override fun toCsvRow(): List<String> = listOf(
        date,
        hour,
        quality,
        person.name
    )

    companion object {
        fun fromCsvRow(row: List<String>): PoopEntry? {
            if (row.size < 4) return null
            return try {
                PoopEntry(
                    date = row[0],
                    hour = row[1],
                    quality = row[2],
                    person = Person.valueOf(row[3])
                )
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}
