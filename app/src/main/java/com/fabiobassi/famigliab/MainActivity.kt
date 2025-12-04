package com.fabiobassi.famigliab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme

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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Budgeting,
        BottomNavItem.GroceryList,
        BottomNavItem.Passwords,
        BottomNavItem.Documents
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = items.find { it.route == currentRoute }

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
                ),
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = { AppBottomNavigationBar(navController = navController, items = items) }
    ) { innerPadding ->
        NavHost(
            navController,
            //startDestination = BottomNavItem.Home.route,
            startDestination = BottomNavItem.Passwords.route,
            //Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Budgeting.route) { BudgetingScreen() }
            composable(BottomNavItem.GroceryList.route) { GroceryListScreen() }
            composable(BottomNavItem.Passwords.route) { PasswordsScreen(innerPadding) }
            composable(BottomNavItem.Documents.route) { DocumentsScreen() }
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
