package com.fabiobassi.famigliab.ui.features.settings

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun ColorSettingDialog(
    person: String,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("color_prefs", Context.MODE_PRIVATE)
    }
    val savedColor = remember {
        sharedPreferences.getString("${person.lowercase()}_color", "") ?: ""
    }
    var color by remember { mutableStateOf(savedColor) }

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
                    with(sharedPreferences.edit()) {
                        putString("${person.lowercase()}_color", color)
                        apply()
                    }
                    onDismissRequest()
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