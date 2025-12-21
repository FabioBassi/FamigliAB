package com.fabiobassi.famigliab.ui.features.budgeting

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.Income
import com.fabiobassi.famigliab.data.Payment
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.Voucher
import com.fabiobassi.famigliab.file.CsvFileManager
import com.fabiobassi.famigliab.file.CsvFileType
import com.fabiobassi.famigliab.ui.features.budgeting.cards.AnnualCategoryExpensesCard
import com.fabiobassi.famigliab.ui.features.budgeting.cards.AnnualMonthRecapCard
import com.fabiobassi.famigliab.ui.features.budgeting.cards.AnnualSummaryCard
import com.fabiobassi.famigliab.ui.features.budgeting.dialogs.AddIncomeDialog
import com.fabiobassi.famigliab.ui.features.budgeting.dialogs.AddPaymentDialog
import com.fabiobassi.famigliab.ui.features.budgeting.dialogs.EditVoucherDialog
import com.fabiobassi.famigliab.ui.features.budgeting.dialogs.IncomeDialog
import com.fabiobassi.famigliab.ui.features.budgeting.dialogs.MonthSelectionPickerDialog
import com.fabiobassi.famigliab.ui.features.budgeting.dialogs.DeleteConfirmationDialog
import com.fabiobassi.famigliab.ui.features.budgeting.cards.CategoryExpensesCard
import com.fabiobassi.famigliab.ui.features.budgeting.cards.LastPaymentsCard
import com.fabiobassi.famigliab.ui.features.budgeting.cards.ShareCard
import com.fabiobassi.famigliab.ui.features.budgeting.cards.SummaryCard
import com.fabiobassi.famigliab.ui.features.budgeting.cards.VoucherSummaryCard
import com.fabiobassi.famigliab.ui.theme.categoryColors
import java.io.File
import java.util.Calendar
import java.util.Date
import java.util.UUID


@Composable
fun BudgetingScreen(
    paddingValues: PaddingValues,
    viewModel: BudgetingViewModel = viewModel(),
) {
    var showMonthSelectionPickerDialog by remember { mutableStateOf(false) }
    var showAddPaymentDialog by remember { mutableStateOf(false) }
    var showAddIncomeDialog by remember { mutableStateOf(false) }
    var showEditVoucherDialog by remember { mutableStateOf(false) }
    var showIncomeDialog by remember { mutableStateOf(false) }
    val payments by viewModel.payments.collectAsState()
    val incomes by viewModel.incomes.collectAsState()
    val vouchers by viewModel.vouchers.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()
    val isAnnualReport by viewModel.isAnnualReport.collectAsState()
    val context = LocalContext.current
    var showAllPayments by remember { mutableStateOf(false) }
    val csvFileManager = remember { CsvFileManager(context) }
    var paymentToDelete by remember { mutableStateOf<Payment?>(null) }

    if (showMonthSelectionPickerDialog) {
        MonthSelectionPickerDialog(
            onDismissRequest = { showMonthSelectionPickerDialog = false },
            onConfirm = { month, year ->
                viewModel.setMonth(month, year)
                showMonthSelectionPickerDialog = false
            }
        )
    }

    if (showAddPaymentDialog) {
        AddPaymentDialog(
            onDismiss = { showAddPaymentDialog = false },
            onConfirm = { date, description, amount, category, person ->
                viewModel.addPayment(date, description, amount, category, person)
                showAddPaymentDialog = false
            }
        )
    }

    paymentToDelete?.let {
        DeleteConfirmationDialog(
            payment = it,
            onConfirm = {
                viewModel.deletePayment(it.id)
                paymentToDelete = null
            },
            onDismiss = { paymentToDelete = null }
        )
    }

    if (showAddIncomeDialog) {
        AddIncomeDialog(
            onDismiss = { showAddIncomeDialog = false },
            onConfirm = { description, amount, person ->
                viewModel.addIncome(description, amount, person)
                showAddIncomeDialog = false
            }
        )
    }

    if (showEditVoucherDialog) {
        EditVoucherDialog(
            vouchers = vouchers,
            onDismiss = { showEditVoucherDialog = false },
            onConfirm = { fabVouchers, sabVouchers, fabVoucherValue, sabVoucherValue ->
                viewModel.updateVouchers(fabVouchers, sabVouchers, fabVoucherValue, sabVoucherValue)
                showEditVoucherDialog = false
            }
        )
    }

    if (showIncomeDialog) {
        IncomeDialog(
            incomes = incomes,
            onDismiss = { showIncomeDialog = false },
            onDeleteIncome = { viewModel.deleteIncome(it) }
        )
    }

    BudgetingScreenContent(
        paddingValues = paddingValues,
        payments = payments,
        incomes = incomes,
        vouchers = vouchers,
        currentDate = currentDate,
        showAllPayments = showAllPayments,
        showAnnualReport = isAnnualReport,
        onShowMonthSelectionClick = { showMonthSelectionPickerDialog = true },
        onShowAllPaymentsClick = { showAllPayments = !showAllPayments },
        onAnnualReportClick = viewModel::toggleAnnualReport,
        onPreviousMonthClick = {
            if (isAnnualReport) {
                viewModel.previousYear()
            } else {
                viewModel.previousMonth()
            }
        },
        onNextMonthClick = {
            if (isAnnualReport) {
                viewModel.nextYear()
            } else {
                viewModel.nextMonth()
            }
        },
        onAddPaymentClick = { showAddPaymentDialog = true },
        onAddIncomeClick = { showAddIncomeDialog = true },
        onSharePaymentsClick = {
            val file = csvFileManager.getFileForMonth(CsvFileType.PAYMENTS, currentDate)
            shareFile(context, file)
        },
        onShareIncomesClick = {
            val file = csvFileManager.getFileForMonth(CsvFileType.INCOMES, currentDate)
            shareFile(context, file)
        },
        onShareVouchersClick = {
            val file = csvFileManager.getFileForMonth(CsvFileType.VOUCHERS, currentDate)
            shareFile(context, file)
        },
        onPaymentLongClick = { paymentToDelete = it },
        onIncomeCardClick = { showIncomeDialog = true },
        onVoucherCardClick = { showEditVoucherDialog = true },
    )
}

@Composable
fun BudgetingScreenContent(
    paddingValues: PaddingValues,
    payments: List<Payment>,
    incomes: List<Income>,
    vouchers: List<Voucher>,
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                NavigationBar(
                    currentDate = currentDate,
                    onMonthClick = onShowMonthSelectionClick,
                    onPreviousMonthClick = onPreviousMonthClick,
                    onNextMonthClick = onNextMonthClick,
                    onAnnualReportClick = onAnnualReportClick,
                    isAnnualReport = showAnnualReport
                )
            }
            if (showAnnualReport) {
                item {
                    AnnualSummaryCard(
                        totalIncomes = annualTotalIncomes,
                        totalExpenses = annualTotalExpenses,
                    )
                }
                item {
                    AnnualCategoryExpensesCard(
                        paymentsByCategory = paymentsByCategory,
                        colors = categoryColors
                    )
                }
                item {
                    AnnualMonthRecapCard()
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
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    onAddPaymentClick()
                },
            ) {
                Icon(Icons.Filled.ShoppingCart, "Add new payment")
            }
            FloatingActionButton(
                onClick = {
                    onAddIncomeClick()
                },
            ) {
                Icon(Icons.Filled.AttachMoney, "Add new income")
            }
        }
    }
}

private fun shareFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share CSV"))
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
        onPaymentLongClick = {},
        onIncomeCardClick = {},
        onVoucherCardClick = {},
    )
}
