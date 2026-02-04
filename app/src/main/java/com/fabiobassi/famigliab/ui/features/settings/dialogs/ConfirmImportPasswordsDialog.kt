package com.fabiobassi.famigliab.ui.features.settings.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme

@Composable
fun ConfirmImportPasswordsDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.import_passwords_title)) },
        text = { Text(stringResource(R.string.import_passwords_message)) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismissRequest()
            }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

@Preview
@Composable
fun ConfirmImportPasswordsDialogPreview() {
    FamigliABTheme {
        ConfirmImportPasswordsDialog(onDismissRequest = {}, onConfirm = {})
    }
}
