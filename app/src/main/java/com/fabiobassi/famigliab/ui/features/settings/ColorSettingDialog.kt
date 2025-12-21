package com.fabiobassi.famigliab.ui.features.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.fabiobassi.famigliab.data.SettingsDataStore
import kotlinx.coroutines.launch

@Composable
fun ColorSettingDialog(
    person: String,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val savedColor by settingsDataStore.getColorFor(person).collectAsState(initial = "")

    var color by remember(savedColor) { mutableStateOf(savedColor) }

    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Set color for $person") },
        text = {
            Column {
                Text("Enter a hex color code (e.g. #FF0000).")
                TextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Hex Color") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch {
                        settingsDataStore.setColorFor(person, color)
                        onDismissRequest()
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
