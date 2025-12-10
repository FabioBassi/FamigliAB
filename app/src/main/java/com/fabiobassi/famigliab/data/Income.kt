package com.fabiobassi.famigliab.data

import java.util.Date

data class Income(
    val description: String,
    val amount: Double,
    val paidTo: Person,
)