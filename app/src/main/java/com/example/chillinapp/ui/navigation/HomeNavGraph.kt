package com.example.chillinapp.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chillinapp.ui.access.login.LogInDestination
import com.example.chillinapp.ui.home.map.MapDestination
import com.example.chillinapp.ui.home.map.MapScreen
import com.example.chillinapp.ui.home.map.MapViewModel
import com.example.chillinapp.ui.home.monitor.MonitorDestination
import com.example.chillinapp.ui.home.monitor.MonitorScreen
import com.example.chillinapp.ui.home.monitor.MonitorViewModel
import com.example.chillinapp.ui.home.settings.SettingsDestination
import com.example.chillinapp.ui.home.settings.SettingsScreen
import com.example.chillinapp.ui.home.settings.SettingsViewModel

/**
 * Navigation graph for the home screen.
 *
 * @param homeNavController the navigation controller
 * @param modifier the modifier for the navigation graph
 */
@Composable
fun HomeNavGraph(
    homeNavController: NavHostController,
    mainNavController: NavHostController,
    monitorViewModel: MonitorViewModel,
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier
) {
    NavHost(
        navController = homeNavController,
        startDestination = MonitorDestination.route,
        modifier = modifier
    ) {

        // Monitor screen route
        composable(
            route = MonitorDestination.route,
            enterTransition = {
                return@composable fadeIn(tween(1000))
            },
            exitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(700)
                )
            },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            }
        ) {
            MonitorScreen(
                viewModel = monitorViewModel
            )
        }

        // Map screen route
        composable(
            route = MapDestination.route,
            enterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(700)
                )
            },
            popExitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                )
            }
        ) {
            MapScreen(
                viewModel = mapViewModel
            )
        }

        // Settings screen route
        composable(route = SettingsDestination.route) {
            SettingsScreen(
                onLogOut = {
                    mainNavController.navigate(LogInDestination.route) {
                        popUpTo(LogInDestination.route) { inclusive = true }
                    }
                },
                onDeletingAccount = {
                    mainNavController.navigate(LogInDestination.route) {
                        popUpTo(LogInDestination.route) { inclusive = true }
                    }
                },
                viewModel = settingsViewModel
            )
        }
    }
}
