package com.fabiobassi.famigliab.ui.features.passwords

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordsScreen() {
    val allPasswords = remember {
        listOf(
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

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val filteredPasswords = if (searchQuery.isBlank()) {
        allPasswords
    } else {
        allPasswords.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.arguments.any { (key, value) ->
                        key.contains(searchQuery, ignoreCase = true) ||
                                value.contains(searchQuery, ignoreCase = true)
                    }
        }
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    if (isSearchActive) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            )
                        )
                    } else {
                        Text(text = "Passwords",
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    if (isSearchActive) {
                        IconButton(onClick = {
                            isSearchActive = false
                            searchQuery = ""
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close search")
                        }
                    } else {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Filled.Search, contentDescription = "Open search")
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Handle new password creation */ }) {
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
            items(filteredPasswords) { password ->
                PasswordCard(password)
            }
        }
    }
}

@Composable
fun PasswordCard(item: PasswordItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            item.arguments.forEach { (key, value) ->
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("$key: ")
                        }
                        append(value)
                    }
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
