package com.fabiobassi.famigliab.data

data class Voucher (
    val value: Double,
    val numberUsed: Int,
    val whose: Person
) : CsvData {

    override fun toCsvRow(): List<String> = listOf(
        value.toString(),
        numberUsed.toString(),
        whose.name
    )

    companion object {
        fun fromCsvRow(row: List<String>): Voucher {
            return Voucher(
                value = row[0].toDouble(),
                numberUsed = row[1].toInt(),
                whose = Person.valueOf(row[2])
            )
        }
    }
}