package com.fabiobassi.famigliab.ui.features.budgeting

import androidx.lifecycle.ViewModel
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.PaidBy
import com.fabiobassi.famigliab.data.Payment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class BudgetingViewModel : ViewModel() {

    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments

    init {
        _payments.value = createMockPayments()
    }

    private fun createMockPayments(): List<Payment> {
        return listOf(
            Payment(Date(), "Spesa Esselunga", 150.55, PaidBy.FAB, Category.CIBO),
            Payment(Date(), "Benzina", 70.0, PaidBy.SAB, Category.MACCHINA),
            Payment(Date(), "Cinema", 20.0, PaidBy.FAB, Category.SVAGO),
            Payment(Date(), "Bolletta luce", 80.0, PaidBy.SAB, Category.BOLLETTE),
        )
    }
}