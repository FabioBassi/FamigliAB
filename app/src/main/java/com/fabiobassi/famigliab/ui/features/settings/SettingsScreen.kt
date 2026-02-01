package com.fabiobassi.famigliab.ui.features.settings

import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatLegroomNormal
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
            SettingsSection(title = "Passwords", icon = Icons.Default.Lock) {
                PreferenceItem(
                    title = "Import passwords.json",
                    icon = Icons.Default.Lock,
                    onClick = { showPasswordResetDialog = true }
                )
            }
        }
    }

    // Dialogs logic
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
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun PreferenceItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    trailing: (@Composable () -> Unit)? = null
) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )
        },
        trailingContent = trailing,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = Modifier
            .clickable { onClick() }
    )
}

@Composable
fun ColorPreview(color: Color) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
    )
}

@Composable
fun PreferenceDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    FamigliABTheme {
        SettingsScreen(paddingValues = PaddingValues(0.dp))
    }
}
