package com.fabiobassi.famigliab.ui.features.budgeting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.Income
import com.fabiobassi.famigliab.data.Payment
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.Voucher
import com.fabiobassi.famigliab.ui.features.budgeting.cards.AnnualCategoryExpensesCard
import com.fabiobassi.famigliab.ui.features.budgeting.cards.AnnualGraph
import com.fabiobassi.famigliab.ui.features.budgeting.cards.AnnualMonthRecap
import com.fabiobassi.famigliab.ui.features.budgeting.cards.AnnualSummaryCard
import com.fabiobassi.famigliab.ui.features.budgeting.cards.CategoryExpensesCard
import com.fabiobassi.famigliab.ui.features.budgeting.cards.LastPaymentsCard
import com.fabiobassi.famigliab.ui.features.budgeting.cards.ShareCard
import com.fabiobassi.famigliab.ui.features.budgeting.cards.SummaryCard
import com.fabiobassi.famigliab.ui.features.budgeting.cards.VoucherSummaryCard
import com.fabiobassi.famigliab.ui.theme.categoryColors
import java.util.Calendar
import java.util.Date
import java.util.UUID

@Composable
fun BudgetingScreenContent(
    paddingValues: PaddingValues,
    payments: List<Payment>,
    incomes: List<Income>,
    vouchers: List<Voucher>,
    fabAnnualIncomes: List<Double>,
    sabAnnualIncomes: List<Double>,
    currentDate: Date,
    showAllPayments: Boolean,
    showAnnualReport: Boolean,
    onShowMonthSelectionClick: () -> Unit,
    onShowAllPaymentsClick: () -> Unit,
    onAnnualReportClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onAddPaymentClick: () -> Unit,
    onAddIncomeClick: () -> Unit,
    onSharePaymentsClick: () -> Unit,
    onShareIncomesClick: () -> Unit,
    onShareVouchersClick: () -> Unit,
    onPaymentClick: (Payment) -> Unit,
    onPaymentLongClick: (Payment) -> Unit,
    onIncomeCardClick: () -> Unit,
    onVoucherCardClick: () -> Unit,
) {
    val selectedMonthCal = Calendar.getInstance().apply { time = currentDate }
    val selectedYear = selectedMonthCal.get(Calendar.YEAR)
    val selectedMonth = selectedMonthCal.get(Calendar.MONTH)

    val paymentsForMonth = remember(payments, currentDate) {
        payments.filter { payment ->
            val itemCalendar = Calendar.getInstance()
            itemCalendar.time = payment.date
            itemCalendar.get(Calendar.MONTH) == selectedMonth && itemCalendar.get(Calendar.YEAR) == selectedYear
        }
    }

    val paymentsForYear = remember(payments, currentDate) {
        payments.filter { payment ->
            val itemCalendar = Calendar.getInstance()
            itemCalendar.time = payment.date
            itemCalendar.get(Calendar.YEAR) == selectedYear
        }
    }

    val totalExpenses = remember(paymentsForMonth) {
        val totalFab = paymentsForMonth.filter { it.paidBy == Person.FAB }.sumOf { it.amount }
        val totalSab = paymentsForMonth.filter { it.paidBy == Person.SAB }.sumOf { it.amount }
        listOf(totalFab + totalSab, totalFab, totalSab)
    }

    val totalIncomes = remember(incomes) {
        val totalFab = incomes.filter { it.paidTo == Person.FAB }.sumOf { it.amount }
        val totalSab = incomes.filter { it.paidTo == Person.SAB }.sumOf { it.amount }
        listOf(totalFab + totalSab, totalFab, totalSab)
    }

    val annualTotalExpenses = remember(paymentsForYear) {
        val totalFab = paymentsForYear.filter { it.paidBy == Person.FAB }.sumOf { it.amount }
        val totalSab = paymentsForYear.filter { it.paidBy == Person.SAB }.sumOf { it.amount }
        listOf(totalFab + totalSab, totalFab, totalSab)
    }

    val annualTotalIncomes = remember(incomes) {
        val totalFab = incomes.filter { it.paidTo == Person.FAB }.sumOf { it.amount }
        val totalSab = incomes.filter { it.paidTo == Person.SAB }.sumOf { it.amount }
        listOf(totalFab + totalSab, totalFab, totalSab)
    }

    val paymentsForPeriod = if (showAnnualReport) paymentsForYear else paymentsForMonth
    val paymentsByCategory = paymentsForPeriod.groupBy { it.category }

    val fabMonthlyPayments = remember(paymentsForYear) {
        val cal = Calendar.getInstance()
        (0..11).map { month ->
            paymentsForYear.filter {
                cal.time = it.date
                cal.get(Calendar.MONTH) == month && it.paidBy == Person.FAB
            }.sumOf { it.amount }
        }
    }
    val sabMonthlyPayments = remember(paymentsForYear) {
        val cal = Calendar.getInstance()
        (0..11).map { month ->
            paymentsForYear.filter {
                cal.time = it.date
                cal.get(Calendar.MONTH) == month && it.paidBy == Person.SAB
            }.sumOf { it.amount }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column {
            NavigationBar(
                modifier = Modifier.padding(16.dp),
                currentDate = currentDate,
                onMonthClick = onShowMonthSelectionClick,
                onPreviousMonthClick = onPreviousMonthClick,
                onNextMonthClick = onNextMonthClick,
                onAnnualReportClick = onAnnualReportClick,
                isAnnualReport = showAnnualReport
            )
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (showAnnualReport) {
                    item {
                        AnnualSummaryCard(
                            totalIncomes = annualTotalIncomes,
                            totalExpenses = annualTotalExpenses,
                        )
                    }
                    item {
                        AnnualGraph(
                            fabMonthlyIncomes = fabAnnualIncomes,
                            fabMonthlyPayments = fabMonthlyPayments,
                            sabMonthlyIncomes = sabAnnualIncomes,
                            sabMonthlyPayments = sabMonthlyPayments
                        )
                    }
                    item {
                        AnnualMonthRecap(
                            fabMonthlyIncomes = fabAnnualIncomes,
                            fabMonthlyPayments = fabMonthlyPayments,
                            sabMonthlyIncomes = sabAnnualIncomes,
                            sabMonthlyPayments = sabMonthlyPayments
                        )
                    }
                    item {
                        AnnualCategoryExpensesCard(
                            paymentsByCategory = paymentsByCategory,
                            colors = categoryColors
                        )
                    }
                } else {
                    item {
                        SummaryCard(
                            totalIncomes = totalIncomes,
                            totalExpenses = totalExpenses,
                            onIncomeCardClick = onIncomeCardClick,
                        )
                    }

                    item {
                        LastPaymentsCard(
                            payments = paymentsForPeriod,
                            colors = categoryColors,
                            showAllPayments = showAllPayments,
                            onShowAllPaymentsClick = onShowAllPaymentsClick,
                            onPaymentClick = onPaymentClick,
                            onPaymentLongClick = onPaymentLongClick,
                        )
                    }

                    if (!showAllPayments) {
                        item {
                            CategoryExpensesCard(
                                paymentsByCategory = paymentsByCategory,
                                colors = categoryColors
                            )
                        }

                        item {
                            VoucherSummaryCard(vouchers = vouchers, onClick = onVoucherCardClick)
                        }
                        item {
                            ShareCard(
                                onSharePaymentsClick = onSharePaymentsClick,
                                onShareIncomesClick = onShareIncomesClick,
                                onShareVouchersClick = onShareVouchersClick
                            )
                        }
                    }
                }
            }
        }
        BudgetingFABs(
            onAddPaymentClick = onAddPaymentClick,
            onAddIncomeClick = onAddIncomeClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 16.dp, vertical = 32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetingScreenPreview() {
    val mockOutcomes = listOf(
        Payment(UUID.randomUUID().toString(), Date(), "Spesa Esselunga", 150.55, Person.FAB, Category.CIBO),
        Payment(UUID.randomUUID().toString(), Date(), "Benzina", 70.0, Person.SAB, Category.MACCHINA),
        Payment(UUID.randomUUID().toString(), Date(), "Cinema", 20.0, Person.FAB, Category.SVAGO),
        Payment(UUID.randomUUID().toString(), Date(), "Bolletta luce", 80.0, Person.SAB, Category.BOLLETTE),
        Payment(UUID.randomUUID().toString(), Date(), "Ristorante", 95.0, Person.FAB, Category.CIBO),
        Payment(UUID.randomUUID().toString(), Date(), "Affitto", 800.0, Person.FAB, Category.CASA),
        Payment(UUID.randomUUID().toString(), Date(), "Spesa abbigliamento", 120.0, Person.SAB, Category.ABBIGLIAMENTO),
        Payment(UUID.randomUUID().toString(), Date(), "Piscina", 45.0, Person.FAB, Category.SPORT),
        Payment(UUID.randomUUID().toString(), Date(), "Visita medica", 60.0, Person.SAB, Category.SALUTE),
        Payment(UUID.randomUUID().toString(), Date(), "Regalo compleanno", 30.0, Person.FAB, Category.REGALI),
        Payment(UUID.randomUUID().toString(), Date(), "Viaggio Roma", 300.0, Person.SAB, Category.TURISMO),
        Payment(UUID.randomUUID().toString(), Date(), "Cena fuori", 75.0, Person.FAB, Category.RISTORAZIONE),
        Payment(UUID.randomUUID().toString(), Date(), "Manutenzione auto", 150.0, Person.SAB, Category.MACCHINA),
        Payment(UUID.randomUUID().toString(), Date(), "Libro", 25.0, Person.FAB, Category.VARIE),
        Payment(UUID.randomUUID().toString(), Date(), "Netflix", 15.99, Person.SAB, Category.SVAGO),
        Payment(UUID.randomUUID().toString(), Date(), "Uscita amici", 50.0, Person.FAB, Category.SVAGO),
        Payment(UUID.randomUUID().toString(), Date(), "Regalo di Natale", 100.0, Person.SAB, Category.REGALI),
        Payment(UUID.randomUUID().toString(), Date(), "Treno", 40.0, Person.FAB, Category.TURISMO),
        Payment(UUID.randomUUID().toString(), Date(), "Spesa Carrefour", 85.30, Person.SAB, Category.CIBO),
        Payment(UUID.randomUUID().toString(), Date(), "Parrucchiere", 40.0, Person.FAB, Category.VARIE)
    )
    val mockIncomes = listOf(
        Income("Stipendio", 2000.0, Person.FAB),
        Income("Stipendio", 1500.0, Person.SAB),
        Income("Bonus", 200.0, Person.FAB),
    )
    val mockVouchers = listOf(
        Voucher(value = 50.0, numberUsed = 2, whose = Person.FAB),
        Voucher(value = 25.0, numberUsed = 1, whose = Person.SAB),
        Voucher(value = 100.0, numberUsed = 1, whose = Person.FAB)
    )
    BudgetingScreenContent(
        paddingValues = PaddingValues(),
        payments = mockOutcomes,
        incomes = mockIncomes,
        vouchers = mockVouchers,
        fabAnnualIncomes = List(12) { 0.0 },
        sabAnnualIncomes = List(12) { 0.0 },
        currentDate = Date(),
        showAllPayments = false,
        showAnnualReport = false,
        onShowMonthSelectionClick = {},
        onShowAllPaymentsClick = {},
        onAnnualReportClick = {},
        onPreviousMonthClick = {},
        onNextMonthClick = {},
        onAddPaymentClick = {},
        onAddIncomeClick = {},
        onSharePaymentsClick = {},
        onShareIncomesClick = {},
        onShareVouchersClick = {},
        onPaymentClick = {},
        onPaymentLongClick = {},
        onIncomeCardClick = {},
        onVoucherCardClick = {},
    )
}
