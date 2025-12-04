package com.fabiobassi.famigliab.ui.features.passwords

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme

@Composable
fun PasswordsScreen(paddingValues: PaddingValues) {
    var showDialog by remember { mutableStateOf(false) }
    var editingPassword by remember { mutableStateOf<PasswordItem?>(null) }
    val allPasswords = remember {
        mutableStateListOf(
            PasswordItem(
                title = "Building Gate",
                arguments = listOf("Code" to "12345")
            ),
            PasswordItem(
                title = "Google",
                arguments = listOf(
                    "Email" to "example@gmail.com",
                    "Username" to "user.name",
                    "Password hash" to "hashedpassword"
                )
            ),
            PasswordItem(
                title = "Home Gate",
                arguments = listOf("Code" to "54321")
            ),
            PasswordItem(
                title = "Facebook",
                arguments = listOf(
                    "Email" to "another@email.com",
                    "Password hash" to "anotherhashedpassword"
                )
            ),
            PasswordItem(
                title = "Facebook",
                arguments = listOf(
                    "Email" to "another@email.com",
                    "Password hash" to "anotherhashedpassword"
                )
            ),
            PasswordItem(
                title = "Facebook",
                arguments = listOf(
                    "Email" to "another@email.com",
                    "Password hash" to "anotherhashedpassword"
                )
            ),
            PasswordItem(
                title = "Facebook",
                arguments = listOf(
                    "Email" to "another@email.com",
                    "Password hash" to "anotherhashedpassword"
                )
            ),
            PasswordItem(
                title = "Facebook",
                arguments = listOf(
                    "Email" to "another@email.com",
                    "Password hash" to "anotherhashedpassword"
                )
            ),
        )
    }

    if (showDialog) {
        AddPasswordDialog(
            onDismiss = { showDialog = false },
            onSave = {
                allPasswords.add(it)
                showDialog = false
            }
        )
    }

    editingPassword?.let { item ->
        EditPasswordDialog(
            item = item,
            onDismiss = { editingPassword = null },
            onSave = { updatedItem ->
                val index = allPasswords.indexOf(item)
                if (index != -1) {
                    allPasswords[index] = updatedItem
                }
                editingPassword = null
            },
            onDelete = {
                allPasswords.remove(item)
                editingPassword = null
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(allPasswords) { password ->
                PasswordCard(
                    item = password,
                    onClick = { editingPassword = password }
                )
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, "Add new password")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordsScreenPreview() {
    FamigliABTheme {
        PasswordsScreen(paddingValues = PaddingValues(0.dp))
    }
}