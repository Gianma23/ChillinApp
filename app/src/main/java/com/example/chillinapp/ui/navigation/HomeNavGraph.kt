package com.example.chillinapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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


object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.home_screen_title
}

@Composable
fun HomeNavGraph(
    modifier: Modifier = Modifier,
) {

    val homeNavController = rememberNavController()

    val items = listOf(
        Screen.Monitor,
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
        startDestination = Screen.Monitor.route,
        modifier = modifier
    ) {

        // Monitor screen route
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
