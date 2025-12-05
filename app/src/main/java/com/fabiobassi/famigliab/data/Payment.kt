package com.fabiobassi.famigliab.data

import java.util.Date

enum class Category {
    ABBIGLIAMENTO,
    BOLLETTE,
    CASA,
    CIBO,
    MACCHINA,
    SALUTE,
    SPORT,
    SVAGO,
    REGALI,
    TURISMO,
    RISTORANTE,
    VARIE
}

enum class PaidBy {
    FAB,
    SAB
}

data class Payment(
    val date: Date,
    val description: String,
    val amount: Double,
    val paidBy: PaidBy,
    val category: Category
)