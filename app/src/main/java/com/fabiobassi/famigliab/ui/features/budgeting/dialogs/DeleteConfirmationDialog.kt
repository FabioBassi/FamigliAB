package com.fabiobassi.famigliab.ui.features.budgeting.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fabiobassi.famigliab.data.Payment
import com.fabiobassi.famigliab.R


@Composable
fun DeleteConfirmationDialog(
    payment: Payment,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_payment)) },
        text = {
            Text(
                stringResource(
                    R.string.delete_payment_confirmation,
                    payment.description,
                    payment.amount
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
