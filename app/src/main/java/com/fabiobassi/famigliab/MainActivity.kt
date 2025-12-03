package com.fabiobassi.famigliab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

sealed class BottomNavItem(val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("Home", Icons.Default.Home)
    object Spese : BottomNavItem("Spese", Icons.AutoMirrored.Filled.TrendingUp)
    object Calendario : BottomNavItem("Calendario", Icons.Default.CalendarToday)
    object ListaSpesa : BottomNavItem("Lista", Icons.Default.ShoppingCart)
    object Documenti : BottomNavItem("Documenti", Icons.Default.Description)
}

@Composable
fun MainScreen() {
    var selectedScreen by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { AppBottomNavigationBar(selectedScreen) { selectedScreen = it } }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (selectedScreen) {
                BottomNavItem.Home -> HomeScreen()
                else -> PlaceholderScreen(screen = selectedScreen)
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(selectedScreen: BottomNavItem, onScreenSelected: (BottomNavItem) -> Unit) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Spese,
        BottomNavItem.Calendario,
        BottomNavItem.ListaSpesa,
        BottomNavItem.Documenti
    )
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selectedScreen == item,
                onClick = { onScreenSelected(item) }
            )
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        WelcomeTitle(name = "Fab")
        InfoWidget(title = "Ultime Spese")
        InfoWidget(title = "Prossimi Eventi")
    }
}

@Composable
fun WelcomeTitle(name: String) {
    Text(
        text = "Ciao $name!",
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
    )
}

@Composable
fun InfoWidget(title: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Nessuna informazione da mostrare.", modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun PlaceholderScreen(screen: BottomNavItem) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Schermata ${screen.title}", fontSize = 24.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    FamigliABTheme {
        MainScreen()
    }
}
