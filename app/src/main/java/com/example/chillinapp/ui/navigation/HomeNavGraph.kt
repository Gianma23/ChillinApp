package com.example.chillinapp.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chillinapp.R
import com.example.chillinapp.ui.home.map.MapDestination
import com.example.chillinapp.ui.home.map.MapScreen
import com.example.chillinapp.ui.home.monitor.MonitorDestination
import com.example.chillinapp.ui.home.monitor.MonitorScreen
import com.example.chillinapp.ui.home.settings.SettingsDestination
import com.example.chillinapp.ui.home.settings.SettingsScreen


object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.home_screen_title
}

@Composable
fun HomeNavGraph(
    modifier: Modifier = Modifier,
) {

    val homeNavController = rememberNavController()

    Scaffold(
        topBar = {
            HomeTopBar(homeNavController)
        },
        bottomBar = {
            HomeBottomBar(
                navController = homeNavController,
                items = listOf(
                    Screen.Monitor,
                    Screen.Map
                )
            )
        },
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { innerPadding ->
        HomeNavHost(
            navController = homeNavController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(navController: NavHostController) {

    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.size(16.dp))
                Text(
                    text = stringResource(id = HomeDestination.titleRes),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                Icon(Screen.Settings.icon, contentDescription = Screen.Settings.label)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
private fun HomeBottomBar(
    navController: NavHostController,
    items: List<Screen>
) {
    BottomAppBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun HomeNavHost(
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Monitor.route,
        modifier = modifier
    ) {

        // Monitor screen route
        composable(route = MonitorDestination.route) {
            MonitorScreen()
        }

        // Map screen route
        composable(route = MapDestination.route) {
            MapScreen()
        }
        
        // Settings screen route
        composable(route = SettingsDestination.route) {
            SettingsScreen()
        }
    }
}
