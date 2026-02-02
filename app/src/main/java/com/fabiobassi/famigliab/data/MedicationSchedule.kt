package com.fabiobassi.famigliab.data

import java.util.UUID

enum class FrequencyType {
    WEEKLY,
    INTERVAL
}

data class MedicationSchedule(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val dosage: String,
    val pillsPerDose: Int = 1,
    val hour: String, // Scheduled time in HH:mm
    val person: Person,
    val frequencyType: FrequencyType = FrequencyType.WEEKLY,
    val daysOfWeek: String? = "MON,TUE,WED,THU,FRI,SAT,SUN", // Comma separated days for WEEKLY
    val intervalDays: Int? = null, // For INTERVAL
    val startDate: String? = null, // For INTERVAL: dd/MM/yyyy
    val isArchived: Boolean = false
) : CsvData {
    override fun toCsvRow(): List<String> {
        return listOf(
            id,
            name,
            dosage,
            pillsPerDose.toString(),
            hour,
            person.name,
            frequencyType.name,
            daysOfWeek ?: "",
            intervalDays?.toString() ?: "",
            startDate ?: "",
            isArchived.toString()
        )
    }

    companion object {
        fun fromCsvRow(row: List<String>): MedicationSchedule? {
            if (row.size < 5) return null
            return try {
                val hasNewFields = row.size >= 10
                MedicationSchedule(
                    id = row[0],
                    name = row[1],
                    dosage = row[2],
                    pillsPerDose = if (hasNewFields) row[3].toIntOrNull() ?: 1 else 1,
                    hour = if (hasNewFields) row[4] else row[3],
                    person = if (hasNewFields) Person.valueOf(row[5]) else Person.valueOf(row[4]),
                    frequencyType = if (hasNewFields) FrequencyType.valueOf(row[6]) else FrequencyType.WEEKLY,
                    daysOfWeek = if (hasNewFields) row[7].ifEmpty { null } else (if (row.size > 5) row[5] else "MON,TUE,WED,THU,FRI,SAT,SUN"),
                    intervalDays = if (hasNewFields) row[8].toIntOrNull() else null,
                    startDate = if (hasNewFields) row[9].ifEmpty { null } else null,
                    isArchived = if (row.size > 10) row[10].toBoolean() else false
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
