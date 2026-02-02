package com.fabiobassi.famigliab.data

data class MedicationEntry(
    val date: String,
    val hour: String,
    val name: String,
    val dosage: String,
    val person: Person,
    val scheduleId: String? = null
) : CsvData {
    override fun toCsvRow(): List<String> {
        return listOf(date, hour, name, dosage, person.name, scheduleId ?: "")
    }

    companion object {
        fun fromCsvRow(row: List<String>): MedicationEntry? {
            if (row.size < 5) return null
            return try {
                MedicationEntry(
                    date = row[0],
                    hour = row[1],
                    name = row[2],
                    dosage = row[3],
                    person = Person.valueOf(row[4]),
                    scheduleId = if (row.size > 5 && row[5].isNotEmpty()) row[5] else null
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
