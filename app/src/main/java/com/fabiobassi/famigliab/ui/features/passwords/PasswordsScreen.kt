package com.fabiobassi.famigliab.ui.features.passwords

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordsScreen() {
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

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

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = "Passwords",
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add new password")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordsScreenPreview() {
    FamigliABTheme {
        PasswordsScreen()
    }
}
