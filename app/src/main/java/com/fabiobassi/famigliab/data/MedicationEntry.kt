package com.fabiobassi.famigliab.data

data class MedicationEntry(
    val date: String,
    val hour: String,
    val name: String,
    val dosage: String,
    val person: Person,
    val scheduleId: String? = null,
    val pillsPerDose: Int = 1
) : CsvData {
    override fun toCsvRow(): List<String> {
        return listOf(date, hour, name, dosage, person.name, scheduleId ?: "", pillsPerDose.toString())
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
                    scheduleId = if (row.size > 5 && row[5].isNotEmpty()) row[5] else null,
                    pillsPerDose = if (row.size > 6) row[6].toIntOrNull() ?: 1 else 1
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
