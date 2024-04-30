package com.example.chillinapp.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.chillinapp.R
import com.example.chillinapp.ui.access.login.LogInDestination
import com.example.chillinapp.ui.navigation.NavigationDestination
import kotlinx.coroutines.delay

object LandingDestination: NavigationDestination {
    override val route: String = "splash"
    override val titleRes: Int = R.string.loading_text
}

private const val SplashWaitingTime: Long = 200L

@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onTimeout: () -> Unit,
) {

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        val currentOnTimeout by rememberUpdatedState(onTimeout)

        LaunchedEffect(Unit) {

            // Simulate a delay to show the splash screen
            delay(SplashWaitingTime)

            // Navigate to the login screen and remove the landing screen from the back stack
            navController.navigate(LogInDestination.route) {
                popUpTo(LandingDestination.route) { inclusive = true }
            }

            currentOnTimeout()
        }

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.chillin_logo_description),
            modifier = Modifier.size(200.dp)
        )

    }

}