package com.fabiobassi.famigliab.ui.features.budgeting

import androidx.compose.runtime.Composable
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.Income
import com.fabiobassi.famigliab.data.Payment
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.Voucher
import com.fabiobassi.famigliab.ui.features.budgeting.dialogs.AddIncomeDialog
import com.fabiobassi.famigliab.ui.features.budgeting.dialogs.AddPaymentDialog
import com.fabiobassi.famigliab.ui.features.budgeting.dialogs.DeleteConfirmationDialog
import com.fabiobassi.famigliab.ui.features.budgeting.dialogs.EditPaymentDialog
import com.fabiobassi.famigliab.ui.features.budgeting.dialogs.EditVoucherDialog
import com.fabiobassi.famigliab.ui.features.budgeting.dialogs.IncomeDialog
import com.fabiobassi.famigliab.ui.features.budgeting.dialogs.MonthSelectionPickerDialog
import java.util.Date

@Composable
fun BudgetingDialogsManager(
    showMonthSelectionPickerDialog: Boolean,
    onMonthSelectionDismiss: () -> Unit,
    onMonthSelectionConfirm: (Int, Int) -> Unit,
    showAddPaymentDialog: Boolean,
    onAddPaymentDismiss: () -> Unit,
    onAddPaymentConfirm: (Date, String, Double, Category, Person) -> Unit,
    paymentToEdit: Payment?,
    onEditPaymentDismiss: () -> Unit,
    onEditPaymentConfirm: (Date, String, Double, Category, Person) -> Unit,
    paymentToDelete: Payment?,
    onDeletePaymentConfirm: (Payment) -> Unit,
    onDeletePaymentDismiss: () -> Unit,
    showAddIncomeDialog: Boolean,
    onAddIncomeDismiss: () -> Unit,
    onAddIncomeConfirm: (String, Double, Person) -> Unit,
    showEditVoucherDialog: Boolean,
    vouchers: List<Voucher>,
    onEditVoucherDismiss: () -> Unit,
    onEditVoucherConfirm: (Int, Int, Double, Double) -> Unit,
    showIncomeDialog: Boolean,
    incomes: List<Income>,
    onIncomeDialogDismiss: () -> Unit,
    onDeleteIncome: (Income) -> Unit,
) {
    if (showMonthSelectionPickerDialog) {
        MonthSelectionPickerDialog(
            onDismissRequest = onMonthSelectionDismiss,
            onConfirm = onMonthSelectionConfirm
        )
    }

    if (showAddPaymentDialog) {
        AddPaymentDialog(
            onDismiss = onAddPaymentDismiss,
            onConfirm = onAddPaymentConfirm
        )
    }

    paymentToEdit?.let { payment ->
        EditPaymentDialog(
            payment = payment,
            onDismiss = onEditPaymentDismiss,
            onConfirm = onEditPaymentConfirm
        )
    }

    paymentToDelete?.let {
        DeleteConfirmationDialog(
            payment = it,
            onConfirm = { onDeletePaymentConfirm(it) },
            onDismiss = onDeletePaymentDismiss
        )
    }

    if (showAddIncomeDialog) {
        AddIncomeDialog(
            onDismiss = onAddIncomeDismiss,
            onConfirm = onAddIncomeConfirm
        )
    }

    if (showEditVoucherDialog) {
        EditVoucherDialog(
            vouchers = vouchers,
            onDismiss = onEditVoucherDismiss,
            onConfirm = onEditVoucherConfirm
        )
    }

    if (showIncomeDialog) {
        IncomeDialog(
            incomes = incomes,
            onDismiss = onIncomeDialogDismiss,
            onDeleteIncome = onDeleteIncome
        )
    }
}
