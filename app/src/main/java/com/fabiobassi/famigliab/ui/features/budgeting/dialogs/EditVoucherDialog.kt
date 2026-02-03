package com.fabiobassi.famigliab.ui.features.budgeting.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.Voucher
import com.fabiobassi.famigliab.R


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
    var fabVoucherValue by remember { mutableStateOf(fabVouchers?.value?.toString() ?: "10.5") }
    var sabVoucherValue by remember { mutableStateOf(sabVouchers?.value?.toString() ?: "8.0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.edit_vouchers),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Fab "+ stringResource(R.string.vouchers), style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = fabVoucherCount,
                    onValueChange = { fabVoucherCount = it },
                    label = { Text(stringResource(R.string.count)) },
                    leadingIcon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = fabVoucherValue,
                    onValueChange = { fabVoucherValue = it },
                    label = { Text(stringResource(R.string.value)) },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    suffix = { Text("€") }
                )

                Text("Sab " + stringResource(R.string.vouchers), style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = sabVoucherCount,
                    onValueChange = { sabVoucherCount = it },
                    label = { Text(stringResource(R.string.count)) },
                    leadingIcon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = sabVoucherValue,
                    onValueChange = { sabVoucherValue = it },
                    label = { Text(stringResource(R.string.value)) },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    suffix = { Text("€") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fabCount = fabVoucherCount.toIntOrNull() ?: 0
                    val sabCount = sabVoucherCount.toIntOrNull() ?: 0
                    val fabValue = fabVoucherValue.replace(",", ".").toDoubleOrNull() ?: 7.0
                    val sabValue = sabVoucherValue.replace(",", ".").toDoubleOrNull() ?: 7.0
                    onConfirm(fabCount, sabCount, fabValue, sabValue)
                }
            ) {
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
