package com.fabiobassi.famigliab.ui.features.passwords

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.window.Dialog
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme

@Composable
fun AddPasswordDialog(
    onDismiss: () -> Unit,
    onSave: (PasswordItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    val fields = remember { mutableStateListOf(Pair("", "")) }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .imePadding(),
            ) {
                Text(
                    text = stringResource(id = R.string.add_new_password),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(id = R.string.title)) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    isError = title.isBlank()
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                ) {
                    itemsIndexed(fields) { index, pair ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    TextField(
                                        value = pair.first,
                                        onValueChange = { fields[index] = it to pair.second },
                                        label = { Text(stringResource(id = R.string.password_field)) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    TextField(
                                        value = pair.second,
                                        onValueChange = { fields[index] = pair.first to it },
                                        label = { Text(stringResource(id = R.string.password_value)) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                IconButton(onClick = { fields.removeAt(index) }) {
                                    Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.delete))
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = R.string.cancel))
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank() && fields.isNotEmpty() && fields.all { it.first.isNotBlank() && it.second.isNotBlank() }) {
                                onSave(PasswordItem(title, fields.toList()))
                            }
                        },
                        enabled = title.isNotBlank() && fields.isNotEmpty() && fields.all { it.first.isNotBlank() && it.second.isNotBlank() },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(stringResource(id = R.string.save))
                    }
                }
            }
        }
    }
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