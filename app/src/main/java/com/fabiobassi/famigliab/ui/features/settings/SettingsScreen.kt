package com.fabiobassi.famigliab.ui.features.settings

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatLegroomNormal
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.data.SettingsDataStore
import com.fabiobassi.famigliab.ui.features.settings.dialogs.ImportPaymentsDialog
import com.fabiobassi.famigliab.ui.features.medications.MedicationsViewModel
import com.fabiobassi.famigliab.ui.features.settings.dialogs.ColorSettingDialog
import com.fabiobassi.famigliab.ui.features.settings.dialogs.ConfirmDeletePoopTrackerDataDialog
import com.fabiobassi.famigliab.ui.features.settings.dialogs.ConfirmImportPasswordsDialog
import com.fabiobassi.famigliab.ui.features.settings.dialogs.ConfirmImportPoopEntriesDialog
import com.fabiobassi.famigliab.ui.features.settings.dialogs.DeleteAllBudgetingDataDialog
import com.fabiobassi.famigliab.ui.features.settings.dialogs.DeleteArchivedMedicationDataDialog
import com.fabiobassi.famigliab.ui.features.settings.dialogs.SharePasswordsDialog
import java.io.File
import java.io.FileOutputStream

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    medicationsViewModel: MedicationsViewModel = viewModel(factory = MedicationsViewModel.Factory)
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val fabColor by settingsDataStore.getColorFor("Fab").collectAsState(initial = "")
    val sabColor by settingsDataStore.getColorFor("Sab").collectAsState(initial = "")

    var showColorSettingDialog by remember { mutableStateOf(false) }
    var personToSetColorFor by remember { mutableStateOf<String?>(null) }
    var showImportPaymentCsvDialog by remember { mutableStateOf(false) }
    var showDeleteAllBudgetingDataDialog by remember { mutableStateOf(false) }
    var showImportPoopEntriesDialog by remember { mutableStateOf(false) }
    var showDeletePoopTrackerCsvDialog by remember { mutableStateOf(false) }
    var showPasswordResetDialog by remember { mutableStateOf(false) }
    var showShareOptionsDialog by remember { mutableStateOf(false) }
    var showDeleteArchivedMedicationsDialog by remember { mutableStateOf(false) }

    val importPasswordsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    val passwordFile = File(context.getExternalFilesDir("FamigliAB"), "passwords.json")
                    FileOutputStream(passwordFile).use { outputStream ->
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

    val errorSavingFileText: String = stringResource(R.string.error_saving_file)
    val saveFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
        uri?.let { outputUri ->
            val file = File(context.getExternalFilesDir("FamigliAB"), "passwords.json")
            if (file.exists()) {
                try {
                    context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                        file.inputStream().copyTo(outputStream)
                    }
                    Toast.makeText(context, R.string.passwords_saved_to_files, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, errorSavingFileText, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, R.string.passwords_file_not_found, Toast.LENGTH_SHORT).show()
            }
        }
        showShareOptionsDialog = false
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            SettingsSection(title = "General", icon = Icons.Default.Settings) {
                PreferenceItem(
                    title = "Set Fab color",
                    icon = Icons.Default.ColorLens,
                    onClick = {
                        personToSetColorFor = "Fab"
                        showColorSettingDialog = true
                    },
                    trailing = { ColorPreview(fabColor) }
                )
                PreferenceDivider()
                PreferenceItem(
                    title = "Set Sab color",
                    icon = Icons.Default.ColorLens,
                    onClick = {
                        personToSetColorFor = "Sab"
                        showColorSettingDialog = true
                    },
                    trailing = { ColorPreview(sabColor) }
                )
            }
        }

        item {
            SettingsSection(title = "Budgeting", icon = Icons.Default.Analytics) {
                PreferenceItem(
                    title = "Import payment csv",
                    icon = Icons.Default.FileUpload,
                    onClick = { showImportPaymentCsvDialog = true }
                )
                PreferenceDivider()
                PreferenceItem(
                    title = "Delete all budgeting data",
                    titleColor = MaterialTheme.colorScheme.error,
                    icon = Icons.Default.DeleteForever,
                    iconColor = MaterialTheme.colorScheme.error,
                    onClick = { showDeleteAllBudgetingDataDialog = true }
                )
            }
        }

        item {
            SettingsSection(title = "Poop tracker", icon = Icons.Default.AirlineSeatLegroomNormal) {
                PreferenceItem(
                    title = "Import poop_entries.csv",
                    icon = Icons.Default.FileUpload,
                    onClick = { showImportPoopEntriesDialog = true }
                )
                PreferenceDivider()
                PreferenceItem(
                    title = "Delete all data",
                    titleColor = MaterialTheme.colorScheme.error,
                    icon = Icons.Default.DeleteForever,
                    iconColor = MaterialTheme.colorScheme.error,
                    onClick = { showDeletePoopTrackerCsvDialog = true }
                )
            }
        }

        item {
            SettingsSection(title = "Medications", icon = Icons.Default.LocalHospital) {
                PreferenceItem(
                    title = "Delete archived schedules and history",
                    titleColor = MaterialTheme.colorScheme.error,
                    icon = Icons.Default.DeleteForever,
                    iconColor = MaterialTheme.colorScheme.error,
                    onClick = { showDeleteArchivedMedicationsDialog = true }
                )
            }
        }

        item {
            SettingsSection(title = "Passwords", icon = Icons.Default.Lock) {
                PreferenceItem(
                    title = "Import passwords.json",
                    icon = Icons.Default.Lock,
                    onClick = { showPasswordResetDialog = true }
                )
                PreferenceDivider()
                PreferenceItem(
                    title = "Share or Save passwords.json",
                    icon = Icons.Default.Share,
                    onClick = { showShareOptionsDialog = true }
                )
            }
        }
    }

    // Dialogs logic
    if (showDeleteArchivedMedicationsDialog) {
        DeleteArchivedMedicationDataDialog(
            onDismissRequest = { showDeleteArchivedMedicationsDialog = false },
            onConfirm = {
                medicationsViewModel.deleteArchivedSchedulesAndHistory()
                Toast.makeText(context, "Archived medication data deleted", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showPasswordResetDialog) {
        ConfirmImportPasswordsDialog(
            onDismissRequest = { showPasswordResetDialog = false },
            onConfirm = {
                importPasswordsLauncher.launch("application/json")
            },
        )
    }

    if (showShareOptionsDialog) {
        val shareFileText = stringResource(R.string.share_file)
        SharePasswordsDialog(
            onDismissRequest = { showShareOptionsDialog = false },
            onShare = {
                val file = File(context.getExternalFilesDir("FamigliAB"), "passwords.json")
                if (file.exists()) {
                    val uri = FileProvider.getUriForFile(context, "com.fabiobassi.famigliab.fileprovider", file)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        type = "application/json"
                        putExtra(Intent.EXTRA_STREAM, uri)
                    }
                    val chooser = Intent.createChooser(intent, shareFileText)
                    context.startActivity(chooser)
                } else {
                    Toast.makeText(context, R.string.passwords_file_not_found, Toast.LENGTH_SHORT).show()
                }
            },
            onSaveToFiles = {
                saveFileLauncher.launch("passwords.json")
            },
            onCopyToClipboard = {
                val file = File(context.getExternalFilesDir("FamigliAB"), "passwords.json")
                if (file.exists()) {
                    val fileContent = file.readText()
                    clipboardManager.setText(AnnotatedString(fileContent))
                    Toast.makeText(context, R.string.passwords_copied_to_clipboard, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, R.string.passwords_file_not_found, Toast.LENGTH_SHORT).show()
                }
            }
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
}

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Column(content = content)
    }
}

@Composable
fun PreferenceItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.padding(end = 16.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )
        trailing?.invoke()
    }
}

@Composable
fun PreferenceDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

@Composable
fun ColorPreview(colorHex: String) {
    val color = if (colorHex.isNotEmpty()) Color(colorHex.toColorInt()) else Color.Transparent
    Surface(
        modifier = Modifier.size(24.dp),
        shape = CircleShape,
        color = color,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {}
}
