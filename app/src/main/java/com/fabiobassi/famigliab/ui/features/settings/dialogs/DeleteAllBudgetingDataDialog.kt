package com.fabiobassi.famigliab.ui.features.settings.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.fabiobassi.famigliab.R
import java.io.File

@Composable
fun DeleteAllBudgetingDataDialog(
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.confirm_deletion)) },
        text = { Text(stringResource(R.string.delete_budgeting_data_confirmation)) },
        confirmButton = {
            TextButton(onClick = {
                val budgetingDir = File(context.getExternalFilesDir("FamigliAB"), "Budgeting")
                if (budgetingDir.exists()) {
                    budgetingDir.deleteRecursively()
                }
                onDismissRequest()
            }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
