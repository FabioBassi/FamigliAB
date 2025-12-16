package com.fabiobassi.famigliab.ui.features.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme
import java.io.File
import java.io.FileOutputStream

@Composable
fun SettingsScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    var showPaswordResetDialog by remember { mutableStateOf(false) }
    var showImportPaymentCsvDialog by remember { mutableStateOf(false) }

    val importPasswordsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues) // Apply outer padding here
            .padding(16.dp) // Apply inner padding for content spacing
    ) {
        Text(
            text = "Home",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Budgeting",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        TextButton(
            onClick = { showImportPaymentCsvDialog = true },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Import payment csv")
        }
        Text(
            text = "Grocery List",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Passwords",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp) // Adjusted padding for the button below
        )
        TextButton(
            onClick = { showPaswordResetDialog = true },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Import Passwords.json")
        }

        if (showPaswordResetDialog) {
            AlertDialog(
                onDismissRequest = { showPaswordResetDialog = false },
                title = { Text("Confirm Import") },
                text = { Text("Are you sure you want to import passwords? This will overwrite existing data.") },
                confirmButton = {
                    TextButton(onClick = {
                        showPaswordResetDialog = false
                        importPasswordsLauncher.launch("application/json")
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPaswordResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showImportPaymentCsvDialog) {
            ImportPaymentsDialog(onDismissRequest = { showImportPaymentCsvDialog = false })
        }

        Text(
            text = "Documents",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
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
