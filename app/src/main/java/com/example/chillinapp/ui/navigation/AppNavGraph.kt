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


/**
 * Composable function that provides the navigation graph for the application.
 *
 * This function creates a NavHost with the start destination set to the LogInScreen. It defines three routes:
 * - LogInDestination: Displays the LogInScreen and provides navigation actions to the SignInScreen and PswRecoveryScreen.
 * - SignInDestination: Displays the SignInScreen and provides a navigation action to the LogInScreen.
 * - PswRecoveryDestination: Displays the PswRecoveryScreen and provides navigation actions to the LogInScreen and SignInScreen.
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
        startDestination = LogInDestination.route,
        modifier = modifier
    ) {
        composable(route = LogInDestination.route) {
            LogInScreen(
                navigateToSignInScreen = { navController.navigate(SignInDestination.route) },
                navigateToPswRecoveryScreen = { navController.navigate(PswRecoveryDestination.route) },
                navigateToHomeScreen = { /*TODO: implement home screen */ }
            )
        }
        composable(route = SignInDestination.route) {
            SignInScreen(
                navigateToLogInScreen = { navController.navigate(LogInDestination.route) }
            )
        }
        composable(route = PswRecoveryDestination.route) {
            PswRecoveryScreen(
                navigateToLogInScreen = { navController.navigate(LogInDestination.route) },
                navigateToSignInScreen = { navController.navigate(SignInDestination.route) }
            )
        }
    }
}