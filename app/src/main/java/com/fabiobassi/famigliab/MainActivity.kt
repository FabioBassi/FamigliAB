package com.fabiobassi.famigliab

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirlineSeatLegroomNormal
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.fabiobassi.famigliab.ui.features.budgeting.BudgetingScreen
import com.fabiobassi.famigliab.ui.features.medications.MedicationsScreen
import com.fabiobassi.famigliab.ui.features.passwords.PasswordsScreen
import com.fabiobassi.famigliab.ui.features.poop_tracker.PoopTrackerScreenContainer
import com.fabiobassi.famigliab.ui.features.settings.SettingsScreen
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
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

sealed class NavItem(val titleResId: Int, val icon: ImageVector, val route: String) {
    object Settings : NavItem(R.string.settings, Icons.Default.Settings, "settings")
    object Budgeting : NavItem(R.string.budgeting, Icons.Default.Analytics, "budgeting")
    object PoopTracker : NavItem(R.string.poop_tracker, Icons.Default.AirlineSeatLegroomNormal, "poop_tracker")
    object Medications : NavItem(R.string.medications, Icons.Default.LocalHospital, "medications")
    object Passwords : NavItem(R.string.passwords, Icons.Default.Lock, "passwords")
}

@Composable
fun MainScreen() {
    val items = listOf(
        NavItem.Settings,
        NavItem.Budgeting,
        NavItem.PoopTracker,
        NavItem.Medications,
        NavItem.Passwords
    )

    val navController = rememberNavController()

    // Initialize the navigation graph to avoid "setGraph() before getGraph()" error
    // This is required because we are using NavController without a NavHost
    navController.graph = remember(items) {
        navController.createGraph(startDestination = items[1].route) {
            items.forEach { item ->
                composable(item.route) {
                    // Empty because UI is handled by HorizontalPager
                }
            }
        }
    }

    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val lazyListState = rememberLazyListState()
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { items.size })

    // Synchronize Pager with Navigation
    LaunchedEffect(pagerState.currentPage) {
        val targetRoute = items[pagerState.currentPage].route
        if (currentRoute != targetRoute) {
            navController.navigate(targetRoute) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    // Synchronize Navigation with Pager (e.g., when clicking a pill or back button)
    LaunchedEffect(currentRoute) {
        val index = items.indexOfFirst { it.route == currentRoute }
        if (index != -1 && index != pagerState.currentPage) {
            pagerState.animateScrollToPage(index)
        }
    }

    // Scroll the pill list when the pager changes
    LaunchedEffect(pagerState.currentPage) {
        lazyListState.animateScrollToItem(pagerState.currentPage)
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            // Merged Navigation and Title using LazyRow
            LazyRow(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = innerPadding.calculateTopPadding() + 24.dp, bottom = 24.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(items) { index, item ->
                    val selected = currentRoute == item.route
                    PillNavigationItem(
                        item = item,
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        }
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                shadowElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        beyondViewportPageCount = 1
                    ) { page ->
                        val screenPadding = PaddingValues(0.dp)
                        when (items[page]) {
                            NavItem.Settings -> SettingsScreen(screenPadding)
                            NavItem.Budgeting -> BudgetingScreen(screenPadding)
                            NavItem.PoopTracker -> PoopTrackerScreenContainer(screenPadding)
                            NavItem.Medications -> MedicationsScreen(screenPadding)
                            NavItem.Passwords -> PasswordsScreen(
                                paddingValues = screenPadding,
                                isVisible = pagerState.currentPage == page
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PillNavigationItem(
    item: NavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val isSettings = item is NavItem.Settings
    
    val selectedBgColor = if (isSettings) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
    val selectedContentColor = if (isSettings) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
    
    val unselectedBgColor = if (isSettings) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val unselectedContentColor = if (isSettings) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) selectedBgColor else unselectedBgColor,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "pillBackground"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) selectedContentColor else unselectedContentColor,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "pillContent"
    )
    val horizontalPadding by animateDpAsState(
        targetValue = if (selected) 24.dp else 16.dp,
        label = "pillPadding"
    )
    val height by animateDpAsState(
        targetValue = if (selected) 56.dp else 44.dp,
        label = "pillHeight"
    )
    val fontSize = if (selected) 20.sp else 14.sp

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        color = backgroundColor,
        contentColor = contentColor,
        modifier = Modifier.height(height)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(if (selected) 24.dp else 18.dp)
            )
            Text(
                text = stringResource(id = item.titleResId),
                fontSize = fontSize,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                style = MaterialTheme.typography.titleLarge
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
