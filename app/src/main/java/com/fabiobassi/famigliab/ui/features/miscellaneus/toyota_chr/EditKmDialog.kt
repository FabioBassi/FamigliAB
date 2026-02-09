package com.fabiobassi.famigliab.ui.features.miscellaneus.toyota_chr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.data.ChrUsage

@Composable
fun EditKmDialog(
    entry: ChrUsage,
    onDismiss: () -> Unit,
    onConfirm: (ChrUsage) -> Unit
) {
    var kmValue by remember { mutableStateOf(entry.actualKm?.toString() ?: "") }
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Actual KM - Week ${entry.week}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Date: ${entry.date}")
                Text(text = "Expected: ${entry.expectedKm} km")
                OutlinedTextField(
                    value = kmValue,
                    onValueChange = { kmValue = it },
                    label = { Text("Actual KM") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val updatedEntry = entry.copy(actualKm = kmValue.toIntOrNull())
                    onConfirm(updatedEntry)
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
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
