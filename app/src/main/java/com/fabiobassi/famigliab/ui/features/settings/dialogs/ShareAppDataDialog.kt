package com.fabiobassi.famigliab.ui.features.settings.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fabiobassi.famigliab.R

@Composable
fun ShareAppDataDialog(
    onDismissRequest: () -> Unit,
    onShare: () -> Unit,
    onSaveToFiles: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Share or Save App Data") },
        text = {
            Column {
                TextButton(onClick = {
                    onShare()
                    onDismissRequest()
                }) {
                    Text(stringResource(id = R.string.share_with_other_apps))
                }

                TextButton(onClick = {
                    onSaveToFiles()
                    onDismissRequest()
                }) {
                    Text(stringResource(id = R.string.save_to_files))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}
