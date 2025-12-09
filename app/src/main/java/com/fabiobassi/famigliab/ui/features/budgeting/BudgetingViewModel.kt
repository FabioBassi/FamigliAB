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

    fun addPayment(date: Date = Date(), description: String, amount: Double, category: Category, paidBy: Person) {
        val newPayment = Payment(
            date = date,
            description = description,
            amount = amount,
            paidBy = paidBy,
            category = category,
        )
        _payments.value += newPayment
    }

    fun addIncome(date: Date = Date(), description: String, amount: Double, paidTo: Person) {
        val newIncome = Income(
            date = date,
            description = description,
            amount = amount,
            paidTo = paidTo
        )
        _incomes.value += newIncome
    }

    private fun createMockPayments(): List<Payment> {
        return listOf(
            Payment(Date(), "Spesa Esselunga", 150.55, Person.FAB, Category.CIBO),
            Payment(Date(), "Benzina", 70.0, Person.SAB, Category.MACCHINA),
            Payment(Date(), "Cinema", 20.0, Person.FAB, Category.SVAGO),
            Payment(Date(), "Bolletta luce", 80.0, Person.SAB, Category.BOLLETTE),
            Payment(Date(), "Ristorante", 95.0, Person.FAB, Category.CIBO),
            Payment(Date(), "Affitto", 800.0, Person.FAB, Category.CASA),
            Payment(Date(), "Spesa abbigliamento", 120.0, Person.SAB, Category.ABBIGLIAMENTO),
            Payment(Date(), "Piscina", 45.0, Person.FAB, Category.SPORT),
            Payment(Date(), "Visita medica", 60.0, Person.SAB, Category.SALUTE),
            Payment(Date(), "Regalo compleanno", 30.0, Person.FAB, Category.REGALI),
            Payment(Date(), "Viaggio Roma", 3000.0, Person.SAB, Category.TURISMO),
            Payment(Date(), "Cena fuori", 75.0, Person.FAB, Category.RISTORANTE),
            Payment(Date(), "Manutenzione auto", 150.0, Person.SAB, Category.MACCHINA),
            Payment(Date(), "Libro", 25.0, Person.FAB, Category.VARIE),
            Payment(Date(), "Netflix", 15.99, Person.SAB, Category.SVAGO),
            Payment(Date(), "Uscita amici", 50.0, Person.FAB, Category.SVAGO),
            Payment(Date(), "Regalo di Natale", 100.0, Person.SAB, Category.REGALI),
            Payment(Date(), "Treno", 40.0, Person.FAB, Category.TURISMO),
            Payment(Date(), "Spesa Carrefour", 85.30, Person.SAB, Category.CIBO),
            Payment(Date(), "Parrucchiere", 40.0, Person.FAB, Category.VARIE)
        )
    }
    private fun createMockIncomes(): List<Income> {
        return listOf(
            Income(Date(), "Stipendio ENAV", amount = 2000.0, paidTo = Person.FAB),
            Income(Date(), "Stipendio FF", amount = 1500.0, paidTo = Person.SAB)
        )
    }
}