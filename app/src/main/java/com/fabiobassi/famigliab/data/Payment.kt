package com.fabiobassi.famigliab.data

import java.util.Date

data class Payment(
    val date: Date,
    val description: String,
    val amount: Double,
    val paidBy: Person,
    val category: Category
)