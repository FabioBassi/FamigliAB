package com.fabiobassi.famigliab.ui.features.passwords.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.ui.features.passwords.PasswordItem
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme

@Composable
fun AddPasswordDialog(
    onDismiss: () -> Unit,
    onSave: (PasswordItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    val fields = remember { mutableStateListOf(Pair("", "")) }

    val isSaveEnabled = title.isNotBlank() &&
            fields.isNotEmpty() &&
            fields.all { it.first.isNotBlank() && it.second.isNotBlank() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.add_new_password),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(id = R.string.title)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = title.isBlank() && title.isNotEmpty(),
                    singleLine = true
                )

                HorizontalDivider()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(fields) { index, pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = pair.first,
                                    onValueChange = { fields[index] = it to pair.second },
                                    label = { Text(stringResource(id = R.string.password_field)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = pair.second,
                                    onValueChange = { fields[index] = pair.first to it },
                                    label = { Text(stringResource(id = R.string.password_value)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                            IconButton(
                                onClick = { fields.removeAt(index) },
                                enabled = fields.size > 1
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(id = R.string.delete),
                                    tint = if (fields.size > 1) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                                )
                            }
                        }
                    }
                    
                    item {
                        OutlinedButton(
                            onClick = { fields.add(Pair("", "")) },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(stringResource(id = R.string.add_field))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isSaveEnabled) {
                        onSave(PasswordItem(title, fields.toList()))
                    }
                },
                enabled = isSaveEnabled
            ) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddPasswordDialogPreview() {
    FamigliABTheme {
        AddPasswordDialog(
            onDismiss = {},
            onSave = {}
        )
    }
}
