package com.example.chillinapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.chillinapp.ui.navigation.ChillInAppNavHost


/**
 * Top level composable that represents screens for the application.
 *
 * This function is a composable that sets up the navigation for the application. It creates a NavHostController
 * using the rememberNavController function, and passes this controller to the ChillInAppNavHost composable.
 *
 * The NavHostController is responsible for managing the navigation within the NavHost. It keeps track of the current
 * navigation stack and provides methods to navigate to different destinations.
 *
 * @param navController The NavHostController for the application. If no controller is provided, a new one is created
 * using the rememberNavController function.
 */
@Composable
fun ChillInApp(navController: NavHostController = rememberNavController()) {
    ChillInAppNavHost(navController = navController)
}
