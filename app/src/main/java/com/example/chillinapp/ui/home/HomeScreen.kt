package com.example.chillinapp.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chillinapp.R
import com.example.chillinapp.ui.access.login.LogInDestination
import com.example.chillinapp.ui.home.map.MapDestination
import com.example.chillinapp.ui.home.map.MapScreen
import com.example.chillinapp.ui.home.monitor.MonitorDestination
import com.example.chillinapp.ui.home.monitor.MonitorScreen
import com.example.chillinapp.ui.home.settings.SettingsDestination
import com.example.chillinapp.ui.home.settings.SettingsScreen
import com.example.chillinapp.ui.navigation.NavigationDestination


object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.home_screen_title
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {

    val homeNavController = rememberNavController()

    val items = listOf(
        Screen.Overall,
        Screen.Map,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            BottomAppBar {
                val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            homeNavController.navigate(screen.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        HomeNavHost(
            navController = homeNavController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun HomeNavHost(
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Overall.route,
        modifier = modifier
    ) {

        // Overall screen route
        composable(route = MonitorDestination.route) {
            MonitorScreen()
        }

        // Settings screen route
        composable(route = SettingsDestination.route) {
            SettingsScreen(
                navigateToLogInScreen = { navController.navigate(LogInDestination.route) },
            )
        }

        // Map screen route
        composable(route = MapDestination.route) {
            MapScreen()
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Overall : Screen(MonitorDestination.route, "Overall", Icons.Default.Home)
    data object Map : Screen(MapDestination.route, "Map", Icons.Default.Map)
    data object Settings : Screen(SettingsDestination.route, "Settings", Icons.Default.Settings)
}
