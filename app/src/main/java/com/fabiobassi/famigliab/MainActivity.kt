package com.fabiobassi.famigliab

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fabiobassi.famigliab.ui.features.budgeting.BudgetingScreen
import com.fabiobassi.famigliab.ui.features.documents.DocumentsScreen
import com.fabiobassi.famigliab.ui.features.grocerylist.GroceryListScreen
import com.fabiobassi.famigliab.ui.features.home.HomeScreen
import com.fabiobassi.famigliab.ui.features.passwords.PasswordsScreen
import com.fabiobassi.famigliab.ui.features.settings.SettingsScreen
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme
import java.io.File

// New imports
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import android.widget.Toast
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FamigliABTheme {
                MainScreen()
            }
        }
    }
}

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Home : BottomNavItem("Home", Icons.Default.Home, "home")
    object Budgeting : BottomNavItem("Bilancio", Icons.Default.Analytics, "budgeting")
    object GroceryList : BottomNavItem("Lista Spesa", Icons.Default.ShoppingCart, "grocery_list")
    object Passwords : BottomNavItem("Passwords", Icons.Default.Lock, "passwords")
    object Documents : BottomNavItem("Documenti", Icons.Default.Description, "documents")
    object Settings : BottomNavItem("Impostazioni", Icons.Default.Settings, "settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Budgeting,
        BottomNavItem.GroceryList,
        BottomNavItem.Passwords,
        BottomNavItem.Documents,
        BottomNavItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = items.find { it.route == currentRoute }

    val showShareOptionsDialog = remember { mutableStateOf(false) }

    val saveFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
        uri?.let { outputUri ->
            val file = File(context.getExternalFilesDir("FamigliAB"), "passwords.json")
            if (file.exists()) {
                try {
                    context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                        file.inputStream().copyTo(outputStream)
                    }
                    Toast.makeText(context, "Passwords saved to files", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error saving file: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Passwords file not found", Toast.LENGTH_SHORT).show()
            }
        }
        showShareOptionsDialog.value = false // Dismiss dialog after operation
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = if (currentScreen?.route == BottomNavItem.Home.route) "Ciao Fab!" else currentScreen?.title ?: "",
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                scrollBehavior = scrollBehavior,
                actions = {
                    if (currentScreen?.route == BottomNavItem.Passwords.route) {
                        IconButton(onClick = {
                            showShareOptionsDialog.value = true
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share Passwords")
                        }
                    }
                }
            )
        },
        bottomBar = { AppBottomNavigationBar(navController = navController, items = items) }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = BottomNavItem.Passwords.route
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Budgeting.route) { BudgetingScreen() }
            composable(BottomNavItem.GroceryList.route) { GroceryListScreen() }
            composable(BottomNavItem.Passwords.route) { PasswordsScreen(innerPadding) }
            composable(BottomNavItem.Documents.route) { DocumentsScreen() }
            composable(BottomNavItem.Settings.route) { SettingsScreen(innerPadding) }
        }

        if (showShareOptionsDialog.value) {
            AlertDialog(
                onDismissRequest = { showShareOptionsDialog.value = false },
                title = { Text("Share or Save Passwords") },
                text = {
                    Column {
                        // Share option
                        TextButton(onClick = {
                            val file = File(context.getExternalFilesDir("FamigliAB"), "passwords.json")
                            if (file.exists()) {
                                val uri = FileProvider.getUriForFile(context, "com.fabiobassi.famigliab.fileprovider", file)
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    type = "application/json"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                }
                                val chooser = Intent.createChooser(intent, "Share File")
                                context.startActivity(chooser)
                            } else {
                                Toast.makeText(context, "Passwords file not found", Toast.LENGTH_SHORT).show()
                            }
                            showShareOptionsDialog.value = false
                        }) {
                            Text("Share with other apps")
                        }

                        // Save to Files option
                        TextButton(onClick = {
                            saveFileLauncher.launch("passwords.json") // Suggests a filename
                            // The dialog will be dismissed in the launcher's callback
                        }) {
                            Text("Save to Files")
                        }

                        // Copy to Clipboard option
                        TextButton(onClick = {
                            val file = File(context.getExternalFilesDir("FamigliAB"), "passwords.json")
                            if (file.exists()) {
                                val fileContent = file.readText()
                                clipboardManager.setText(AnnotatedString(fileContent))
                                Toast.makeText(context, "Passwords copied to clipboard", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Passwords file not found", Toast.LENGTH_SHORT).show()
                            }
                            showShareOptionsDialog.value = false
                        }) {
                            Text("Copy to Clipboard")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showShareOptionsDialog.value = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun AppBottomNavigationBar(navController: NavController, items: List<BottomNavItem>) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                //label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    FamigliABTheme {
        MainScreen()
    }
}