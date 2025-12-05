package com.fabiobassi.famigliab.ui.features.budgeting

import androidx.lifecycle.ViewModel
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.Income
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.Payment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class BudgetingViewModel : ViewModel() {

    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments
    private val _incomes = MutableStateFlow<List<Income>>(emptyList())
    val incomes: StateFlow<List<Income>> = _incomes

    init {
        _payments.value = createMockPayments()
        _incomes.value = createMockIncomes()
    }

    private fun createMockPayments(): List<Payment> {
        return listOf(
            Payment(Date(), "Spesa Esselunga", 150.55, Person.FAB, Category.CIBO),
            Payment(Date(), "Benzina", 70.0, Person.SAB, Category.MACCHINA),
            Payment(Date(), "Cinema", 20.0, Person.FAB, Category.SVAGO),
            Payment(Date(), "Bolletta luce", 80.0, Person.SAB, Category.BOLLETTE),
        )
    }
    private fun createMockIncomes(): List<Income> {
        return listOf(
            Income(Date(), "Stipendio ENAV", amount = 2000.0, paidTo = Person.FAB),
            Income(Date(), "Stipendio FF", amount = 1500.0, paidTo = Person.SAB)
        )
    }
}