package com.fabiobassi.famigliab.ui.features.budgeting

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiobassi.famigliab.data.Payment
import com.fabiobassi.famigliab.file.CsvFileManager
import com.fabiobassi.famigliab.file.CsvFileType
import java.io.File
import com.fabiobassi.famigliab.R


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
    var paymentToEdit by remember { mutableStateOf<Payment?>(null) }

    BudgetingDialogsManager(
        showMonthSelectionPickerDialog = showMonthSelectionPickerDialog,
        onMonthSelectionDismiss = { showMonthSelectionPickerDialog = false },
        onMonthSelectionConfirm = { month, year ->
            viewModel.setMonth(month, year)
            showMonthSelectionPickerDialog = false
        },
        showAddPaymentDialog = showAddPaymentDialog,
        onAddPaymentDismiss = { showAddPaymentDialog = false },
        onAddPaymentConfirm = { date, description, amount, category, person ->
            viewModel.addPayment(date, description, amount, category, person)
            showAddPaymentDialog = false
        },
        paymentToEdit = paymentToEdit,
        onEditPaymentDismiss = { paymentToEdit = null },
        onEditPaymentConfirm = { date, description, amount, category, person ->
            paymentToEdit?.let { payment ->
                val updatedPayment = payment.copy(
                    date = date,
                    description = description,
                    amount = amount,
                    category = category,
                    paidBy = person
                )
                viewModel.updatePayment(payment, updatedPayment)
            }
            paymentToEdit = null
        },
        paymentToDelete = paymentToDelete,
        onDeletePaymentConfirm = {
            viewModel.deletePayment(it.id)
            paymentToDelete = null
        },
        onDeletePaymentDismiss = { paymentToDelete = null },
        showAddIncomeDialog = showAddIncomeDialog,
        onAddIncomeDismiss = { showAddIncomeDialog = false },
        onAddIncomeConfirm = { description, amount, person ->
            viewModel.addIncome(description, amount, person)
            showAddIncomeDialog = false
        },
        showEditVoucherDialog = showEditVoucherDialog,
        vouchers = vouchers,
        onEditVoucherDismiss = { showEditVoucherDialog = false },
        onEditVoucherConfirm = { fabVouchers, sabVouchers, fabVoucherValue, sabVoucherValue ->
            viewModel.updateVouchers(fabVouchers, sabVouchers, fabVoucherValue, sabVoucherValue)
            showEditVoucherDialog = false
        },
        showIncomeDialog = showIncomeDialog,
        incomes = incomes,
        onIncomeDialogDismiss = { showIncomeDialog = false },
        onDeleteIncome = { viewModel.deleteIncome(it) }
    )

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
        onPaymentClick = { paymentToEdit = it },
        onPaymentLongClick = { paymentToDelete = it },
        onIncomeCardClick = { showIncomeDialog = true },
        onVoucherCardClick = { showEditVoucherDialog = true },
    )
}

private fun shareFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_csv)))
}
