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
import com.example.chillinapp.ui.home.HomeDestination
import com.example.chillinapp.ui.home.HomeScreen
import com.example.chillinapp.ui.splash.LandingDestination
import com.example.chillinapp.ui.splash.LandingScreen


/**
 * Composable function that provides the navigation graph for the application.
 *
 * This function creates a NavHost with the start destination set to the LandingScreen. It defines several routes:
 * - [LandingDestination]: Displays the LandingScreen and provides navigation action to the LogInScreen.
 * - [LogInDestination]: Displays the LogInScreen and provides navigation actions to the SignInScreen, PswRecoveryScreen and OverallScreen.
 * - [SignInDestination]: Displays the SignInScreen and provides a navigation action to the LogInScreen.
 * - [PswRecoveryDestination]: Displays the PswRecoveryScreen and provides navigation actions to the LogInScreen and SignInScreen.
 * - [HomeDestination]: Displays the HomeScreen.
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
                ifLogged = { navController.navigate(HomeDestination.route){
                    navController.popBackStack()
                    popUpTo(LandingDestination.route) { inclusive = true }
                } },
                ifNotLogged = { navController.navigate(LogInDestination.route) {
                    navController.popBackStack()
                    popUpTo(LandingDestination.route) { inclusive = true }
                } }
            )
        }

        // Login screen route
        composable(route = LogInDestination.route) {
            LogInScreen(
                navigateToSignInScreen = { navController.navigate(SignInDestination.route) },
                navigateToPswRecoveryScreen = { navController.navigate(PswRecoveryDestination.route) },
                screenIfSuccess = {
                    navController.popBackStack()
                    navController.navigate(HomeDestination.route) {
                        popUpTo(LogInDestination.route) { inclusive = true }
                    }
                }
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

        // Home screen route
        composable(route = HomeDestination.route) {
            HomeScreen()
        }

    }
}