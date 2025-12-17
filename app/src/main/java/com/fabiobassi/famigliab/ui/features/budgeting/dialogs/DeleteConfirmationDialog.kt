package com.fabiobassi.famigliab.ui.features.budgeting.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.fabiobassi.famigliab.data.Payment

@Composable
fun DeleteConfirmationDialog(
    payment: Payment,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Payment") },
        text = { Text("Are you sure you want to delete this payment?\n'${payment.description}' of ${payment.amount}€") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}