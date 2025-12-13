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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme

@Composable
fun PasswordsScreen(paddingValues: PaddingValues) {
    var showDialog by remember { mutableStateOf(false) }
    var editingPassword by remember { mutableStateOf<PasswordItem?>(null) }
    val context = LocalContext.current
    val repository = remember { PasswordRepository(context) }
    val allPasswords = remember {
        mutableStateListOf<PasswordItem>().also {
            it.addAll(repository.loadPasswords().sortedBy { password -> password.title })
        }
    }

    LaunchedEffect(allPasswords.size) {
        repository.savePasswords(allPasswords)
    }

    if (showDialog) {
        AddPasswordDialog(
            onDismiss = { showDialog = false },
            onSave = {
                allPasswords.add(it)
                allPasswords.sortBy { password -> password.title }
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
                    allPasswords.sortBy { password -> password.title }
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
            Icon(Icons.Filled.Add, stringResource(id = R.string.add_new_password))
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
