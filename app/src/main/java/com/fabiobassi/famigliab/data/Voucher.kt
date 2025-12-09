package com.fabiobassi.famigliab.data

data class Voucher (
    val value: Double,
    val numberUsed: Int,
    val whose: Person
)