package com.fabiobassi.famigliab.ui.features.settings

import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.SettingsDataStore
import com.fabiobassi.famigliab.ui.features.settings.dialogs.ColorSettingDialog
import com.fabiobassi.famigliab.ui.features.settings.dialogs.ConfirmDeletePoopTrackerDataDialog
import com.fabiobassi.famigliab.ui.features.settings.dialogs.ConfirmImportPasswordsDialog
import com.fabiobassi.famigliab.ui.features.settings.dialogs.ConfirmImportPoopEntriesDialog
import com.fabiobassi.famigliab.ui.features.settings.dialogs.DeleteAllBudgetingDataDialog
import com.fabiobassi.famigliab.ui.features.settings.dialogs.ImportPaymentsDialog
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme
import java.io.File
import java.io.FileOutputStream

@Composable
fun SettingsScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    var showPasswordResetDialog by remember { mutableStateOf(false) }
    var showImportPaymentCsvDialog by remember { mutableStateOf(false) }
    var showImportPoopEntriesDialog by remember { mutableStateOf(false) }
    var showDeleteAllBudgetingDataDialog by remember { mutableStateOf(false) }
    var showDeletePoopTrackerCsvDialog by remember { mutableStateOf(false) }
    var showColorSettingDialog by remember { mutableStateOf(false) }
    var personToSetColorFor by remember { mutableStateOf<String?>(null) }

    val fabColorHex by settingsDataStore.getColorFor("Fab").collectAsState(initial = "")
    val sabColorHex by settingsDataStore.getColorFor("Sab").collectAsState(initial = "")

    val fabColor = remember(fabColorHex) {
        try {
            if (fabColorHex.isNotEmpty()) {
                Color(android.graphics.Color.parseColor(fabColorHex))
            } else {
                Color.Transparent
            }
        } catch (e: Exception) {
            Color.Transparent
        }
    }
    val sabColor = remember(sabColorHex) {
        try {
            if (sabColorHex.isNotEmpty()) {
                Color(android.graphics.Color.parseColor(sabColorHex))
            } else {
                Color.Transparent
            }
        } catch (e: Exception) {
            Color.Transparent
        }
    }

    val importPasswordsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    val passwordsFile = File(context.getExternalFilesDir("FamigliAB"), "passwords.json")
                    FileOutputStream(passwordsFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    Toast.makeText(context, "Passwords imported successfully!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error importing passwords: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    val importPoopEntriesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(it)
            var fileName: String? = null
            contentResolver.query(it, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        fileName = cursor.getString(displayNameIndex)
                    }
                }
            }

            if (mimeType == "text/csv" || mimeType == "text/comma-separated-values" || fileName?.endsWith(".csv", ignoreCase = true) == true) {
                try {
                    context.contentResolver.openInputStream(it)?.use { inputStream ->
                        val poopFile =
                            File(context.getExternalFilesDir("FamigliAB/PoopTracker"), "poop_entries.csv")
                        FileOutputStream(poopFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                        Toast.makeText(context, "Poop entries imported successfully!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Error importing poop entries: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(context, "Invalid file type. Please select a CSV file.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues) // Apply outer padding here
            .padding(16.dp), // Apply inner padding for content spacing
    ) {
        Text(
            text = "General",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            TextButton(
                onClick = {
                    personToSetColorFor = "Fab"
                    showColorSettingDialog = true
                },
            ) {
                Text("Set Fab color")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(fabColor)
                    .border(1.dp, Color.Black),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            TextButton(
                onClick = {
                    personToSetColorFor = "Sab"
                    showColorSettingDialog = true
                },
            ) {
                Text("Set Sab color")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(sabColor)
                    .border(1.dp, Color.Black),
            )
        }
        Text(
            text = "Budgeting",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        TextButton(
            onClick = { showImportPaymentCsvDialog = true },
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            Text("Import payment csv")
        }
        TextButton(
            onClick = { showDeleteAllBudgetingDataDialog = true },
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            Text(
                text = "Delete all budgeting data",
                color = MaterialTheme.colorScheme.error,
            )
        }
        Text(
            text = "Poop tracker",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        TextButton(
            onClick = { showImportPoopEntriesDialog = true },
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            Text("Import poop_entries.csv")
        }
        TextButton(
            onClick = { showDeletePoopTrackerCsvDialog = true },
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            Text(
                text = "Delete all data",
                color = MaterialTheme.colorScheme.error,
            )
        }
        Text(
            text = "Passwords",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp), // Adjusted padding for the button below
        )
        TextButton(
            onClick = { showPasswordResetDialog = true },
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            Text("Import passwords.json")
        }

        if (showPasswordResetDialog) {
            ConfirmImportPasswordsDialog(
                onDismissRequest = { showPasswordResetDialog = false },
                onConfirm = {
                    importPasswordsLauncher.launch("application/json")
                },
            )
        }

        if (showImportPoopEntriesDialog) {
            ConfirmImportPoopEntriesDialog(
                onDismissRequest = { showImportPoopEntriesDialog = false },
                onConfirm = {
                    importPoopEntriesLauncher.launch(arrayOf("text/csv", "text/comma-separated-values"))
                },
            )
        }

        if (showImportPaymentCsvDialog) {
            ImportPaymentsDialog(onDismissRequest = { showImportPaymentCsvDialog = false })
        }

        if (showDeleteAllBudgetingDataDialog) {
            DeleteAllBudgetingDataDialog(onDismissRequest = {
                showDeleteAllBudgetingDataDialog = false
            })
        }

        if (showDeletePoopTrackerCsvDialog) {
            ConfirmDeletePoopTrackerDataDialog(
                onDismissRequest = { showDeletePoopTrackerCsvDialog = false },
                onConfirm = {
                    try {
                        val poopFile = File(context.getExternalFilesDir("FamigliAB/PoopTracker"), "poop_entries.csv")
                        if (poopFile.exists()) {
                            poopFile.delete()
                            Toast.makeText(context, "Poop tracker data deleted successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "No poop tracker data found.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error deleting poop tracker data: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                },
            )
        }

        if (showColorSettingDialog) {
            personToSetColorFor?.let {
                ColorSettingDialog(
                    person = it,
                    onDismissRequest = { showColorSettingDialog = false },
                )
            }
        }

        Text(
            text = "Documents",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    FamigliABTheme {
        SettingsScreen(paddingValues = PaddingValues(0.dp))
    }
}
