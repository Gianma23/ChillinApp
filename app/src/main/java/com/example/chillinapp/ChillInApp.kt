package com.example.chillinapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.chillinapp.ui.navigation.ChillInAppNavHost

/**
 * Top level composable that represents screens for the application.
 */
@Composable
fun ChillInApp(navController: NavHostController = rememberNavController()) {
    ChillInAppNavHost(navController = navController)
}
