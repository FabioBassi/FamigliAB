package com.fabiobassi.famigliab.ui.features.settings.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme

@Composable
fun ConfirmImportPoopEntriesDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Confirm Import") },
        text = { Text("Are you sure you want to import poop entries? This will overwrite existing data.") },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismissRequest()
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
    )
}

@Preview
@Composable
fun ConfirmImportPoopEntriesDialogPreview() {
    FamigliABTheme {
        ConfirmImportPoopEntriesDialog(onDismissRequest = {}, onConfirm = {})
    }
}
