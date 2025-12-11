package com.fabiobassi.famigliab.ui.features.budgeting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.Income
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.Payment
import com.fabiobassi.famigliab.data.Voucher
import com.fabiobassi.famigliab.file.CsvFileManager
import com.fabiobassi.famigliab.file.CsvFileType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar
import java.util.Date

class BudgetingViewModel(application: Application) : AndroidViewModel(application) {

    private val csvFileManager = CsvFileManager(application.applicationContext)

    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments
    private val _incomes = MutableStateFlow<List<Income>>(emptyList())
    val incomes: StateFlow<List<Income>> = _incomes
    private val _vouchers = MutableStateFlow<List<Voucher>>(emptyList())
    val vouchers: StateFlow<List<Voucher>> = _vouchers

    private val _currentDate = MutableStateFlow(Date())
    val currentDate: StateFlow<Date> = _currentDate

    init {
        loadDataForMonth(_currentDate.value)
    }

    private fun loadDataForMonth(date: Date) {
        _payments.value = csvFileManager.readData(CsvFileType.PAYMENTS, date, Payment::fromCsvRow)
        _incomes.value = csvFileManager.readData(CsvFileType.INCOMES, date, Income::fromCsvRow)
        _vouchers.value = csvFileManager.readData(CsvFileType.VOUCHERS, date, Voucher::fromCsvRow)
    }

    fun addPayment(date: Date, description: String, amount: Double, category: Category, paidBy: Person) {
        val newPayment = Payment(
            date = date,
            description = description,
            amount = amount,
            paidBy = paidBy,
            category = category,
        )

        val paymentCalendar = Calendar.getInstance().apply { time = date }
        val currentCalendar = Calendar.getInstance().apply { time = _currentDate.value }

        if (paymentCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
            paymentCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)) {
            val updatedPayments = _payments.value + newPayment
            _payments.value = updatedPayments
            csvFileManager.writeData(CsvFileType.PAYMENTS, _currentDate.value, updatedPayments)
        } else {
            val otherMonthPayments = csvFileManager.readData(CsvFileType.PAYMENTS, date, Payment::fromCsvRow)
            val updatedOtherMonthPayments = otherMonthPayments + newPayment
            csvFileManager.writeData(CsvFileType.PAYMENTS, date, updatedOtherMonthPayments)
        }
    }

    fun addIncome(description: String, amount: Double, paidTo: Person) {
        val newIncome = Income(
            description = description,
            amount = amount,
            paidTo = paidTo,
        )
        val updatedIncomes = _incomes.value + newIncome
        _incomes.value = updatedIncomes
        csvFileManager.writeData(CsvFileType.INCOMES, _currentDate.value, updatedIncomes)
    }

    fun addVoucher(value: Double, numberUsed: Int, whose: Person) {
        val newVoucher = Voucher(
            value = value,
            numberUsed = numberUsed,
            whose = whose,
        )
        val updatedVouchers = _vouchers.value + newVoucher
        _vouchers.value = updatedVouchers
        csvFileManager.writeData(CsvFileType.VOUCHERS, _currentDate.value, updatedVouchers)
    }

    fun nextMonth() {
        val calendar = Calendar.getInstance()
        calendar.time = _currentDate.value
        calendar.add(Calendar.MONTH, 1)
        _currentDate.value = calendar.time
        loadDataForMonth(_currentDate.value)
    }

    fun previousMonth() {
        val calendar = Calendar.getInstance()
        calendar.time = _currentDate.value
        calendar.add(Calendar.MONTH, -1)
        _currentDate.value = calendar.time
        loadDataForMonth(_currentDate.value)
    }
}