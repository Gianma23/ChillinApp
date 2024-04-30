package com.example.chillinapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chillinapp.ui.access.login.LogInDestination
import com.example.chillinapp.ui.access.login.LogInScreen
import com.example.chillinapp.ui.access.recovery.PswRecoveryDestination
import com.example.chillinapp.ui.access.recovery.PswRecoveryScreen
import com.example.chillinapp.ui.access.registration.SignInDestination
import com.example.chillinapp.ui.access.registration.SignInScreen
import com.example.chillinapp.ui.home.map.MapDestination
import com.example.chillinapp.ui.home.map.MapScreen
import com.example.chillinapp.ui.home.overall.OverallDestination
import com.example.chillinapp.ui.home.overall.OverallScreen
import com.example.chillinapp.ui.home.settings.SettingsDestination
import com.example.chillinapp.ui.home.settings.SettingsScreen
import com.example.chillinapp.ui.splash.LandingScreen
import com.example.chillinapp.ui.splash.LandingDestination


/**
 * Composable function that provides the navigation graph for the application.
 *
 * This function creates a NavHost with the start destination set to the LandingScreen. It defines several routes:
 * - LandingDestination: Displays the LandingScreen and provides navigation action to the LogInScreen.
 * - LogInDestination: Displays the LogInScreen and provides navigation actions to the SignInScreen, PswRecoveryScreen and OverallScreen.
 * - SignInDestination: Displays the SignInScreen and provides a navigation action to the LogInScreen.
 * - PswRecoveryDestination: Displays the PswRecoveryScreen and provides navigation actions to the LogInScreen and SignInScreen.
 * - OverallDestination: Displays the OverallScreen and provides navigation actions to the SettingsScreen and MapScreen.
 * - SettingsDestination: Displays the SettingsScreen and provides navigation actions to the LogInScreen and OverallScreen.
 * - MapDestination: Displays the MapScreen and provides navigation actions to the SettingsScreen and OverallScreen.
 *
 * @param navController The NavHostController that controls the navigation within the NavHost.
 * @param modifier The Modifier to be applied to the NavHost.
 */
@Composable
fun ChillInAppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = LandingDestination.route,
        modifier = modifier
    ) {

        // Landing screen route
        composable(route = LandingDestination.route) {
            LandingScreen(
                onTimeout = { navController.navigate(LogInDestination.route) },
                navController = navController
            )
        }

        // Login screen route
        composable(route = LogInDestination.route) {
            LogInScreen(
                navigateToSignInScreen = { navController.navigate(SignInDestination.route) },
                navigateToPswRecoveryScreen = { navController.navigate(PswRecoveryDestination.route) },
                navigateToHomeScreen = { navController.navigate(OverallDestination.route) }
            )
        }

        // Sign in screen route
        composable(route = SignInDestination.route) {
            SignInScreen(
                navigateToLogInScreen = { navController.navigate(LogInDestination.route) }
            )
        }

        // Password recovery screen route
        composable(route = PswRecoveryDestination.route) {
            PswRecoveryScreen(
                navigateToLogInScreen = { navController.navigate(LogInDestination.route) },
                navigateToSignInScreen = { navController.navigate(SignInDestination.route) }
            )
        }

        // Overall screen route
        composable(route = OverallDestination.route) {
            OverallScreen(
                navigateToSettingsScreen = { navController.navigate(SettingsDestination.route) },
                navigateToMapScreen = { navController.navigate(MapDestination.route) }
            )
        }

        // Settings screen route
        composable(route = SettingsDestination.route) {
            SettingsScreen(
                navigateToLogInScreen = { navController.navigate(LogInDestination.route) },
                navigateToOverallScreen = { navController.navigate(OverallDestination.route) }
            )
        }

        // Map screen route
        composable(route = MapDestination.route) {
            MapScreen(
                navigateToSettingsScreen = { navController.navigate(SettingsDestination.route) },
                navigateToOverallScreen = { navController.navigate(OverallDestination.route) }
            )
        }
    }
}