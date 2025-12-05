package com.fabiobassi.famigliab.data

import java.util.Date

data class Income(
    val date: Date,
    val description: String,
    val amount: Double,
    val paidTo: Person,
)