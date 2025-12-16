
package com.fabiobassi.famigliab.ui.features.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.File

@Composable
fun DeleteAllDataDialog(
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete all budgeting data? This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = {
                val budgetingDir = File(context.getExternalFilesDir("FamigliAB"), "Budgeting")
                if (budgetingDir.exists()) {
                    budgetingDir.deleteRecursively()
                }
                onDismissRequest()
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
