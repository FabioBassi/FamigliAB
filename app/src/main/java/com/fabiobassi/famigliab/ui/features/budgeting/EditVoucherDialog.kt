package com.fabiobassi.famigliab.ui.features.budgeting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.Voucher

@Composable
fun EditVoucherDialog(
    vouchers: List<Voucher>,
    onDismiss: () -> Unit,
    onConfirm: (fabVouchers: Int, sabVouchers: Int, fabVoucherValue: Double, sabVoucherValue: Double) -> Unit
) {
    val fabVouchers = vouchers.firstOrNull { it.whose == Person.FAB }
    val sabVouchers = vouchers.firstOrNull { it.whose == Person.SAB }

    var fabVoucherCount by remember { mutableStateOf(fabVouchers?.numberUsed?.toString() ?: "0") }
    var sabVoucherCount by remember { mutableStateOf(sabVouchers?.numberUsed?.toString() ?: "0") }
    var fabVoucherValue by remember { mutableStateOf(fabVouchers?.value?.toString() ?: "7.0") }
    var sabVoucherValue by remember { mutableStateOf(sabVouchers?.value?.toString() ?: "7.0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Vouchers") },
        text = {
            Column {
                TextField(
                    value = fabVoucherCount,
                    onValueChange = { fabVoucherCount = it },
                    label = { Text("Fab Vouchers Used") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = fabVoucherValue,
                    onValueChange = { fabVoucherValue = it },
                    label = { Text("Fab Voucher Value") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = sabVoucherCount,
                    onValueChange = { sabVoucherCount = it },
                    label = { Text("Sab Vouchers Used") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = sabVoucherValue,
                    onValueChange = { sabVoucherValue = it },
                    label = { Text("Sab Voucher Value") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val fabCount = fabVoucherCount.toIntOrNull() ?: 0
                val sabCount = sabVoucherCount.toIntOrNull() ?: 0
                val fabValue = fabVoucherValue.toDoubleOrNull() ?: 7.0
                val sabValue = sabVoucherValue.toDoubleOrNull() ?: 7.0
                onConfirm(fabCount, sabCount, fabValue, sabValue)
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EditVoucherDialogPreview() {
    val mockVouchers = listOf(
        Voucher(value = 7.0, numberUsed = 5, whose = Person.FAB),
        Voucher(value = 7.0, numberUsed = 10, whose = Person.SAB)
    )
    EditVoucherDialog(
        vouchers = mockVouchers,
        onDismiss = {},
        onConfirm = { _, _, _, _ -> }
    )
}
