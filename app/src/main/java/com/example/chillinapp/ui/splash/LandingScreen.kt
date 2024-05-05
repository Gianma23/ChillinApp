package com.example.chillinapp.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.AppViewModelProvider
import com.example.chillinapp.ui.navigation.NavigationDestination
import kotlinx.coroutines.delay

/**
 * Object representing the landing destination in the navigation graph.
 *
 * This object provides the route and title for the landing screen.
 */
object LandingDestination: NavigationDestination {
    override val route: String = "splash"
    override val titleRes: Int = R.string.loading_text
}

private const val SplashWaitingTime: Long = 200L

/**
 * Composable function representing the landing screen.
 *
 * This function displays a splash screen with a logo and a welcome message if the user is logged in.
 * It uses a LandingViewModel to check if the user is logged in and to get the user's name.
 * If the user is not logged in, it calls the ifNotLogged function after a delay.
 * If the user is logged in, it calls the ifLogged function after a delay.
 *
 * @param modifier The Modifier to be applied to the Box.
 * @param ifLogged The function to be called if the user is logged in.
 * @param ifNotLogged The function to be called if the user is not logged in.
 * @param viewModel The LandingViewModel to be used. Defaults to a new instance created with the AppViewModelProvider.Factory.
 */
@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    ifLogged: () -> Unit,
    ifNotLogged: () -> Unit,
    viewModel: LandingViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        val uiState = viewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {

            // Check if the user is logged in
            viewModel.isLogged()

            // If the user is not logged in, call the ifNotLogged function after a delay
            if(uiState.value.login?.success != true){
                delay(SplashWaitingTime)
                ifNotLogged()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(R.string.chillin_logo_description),
                modifier = Modifier.size(200.dp)
            )
            AnimatedVisibility(uiState.value.login?.success == true){
                Text(
                    text = "Welcome back " + uiState.value.login?.data?.name + "!",
                    modifier = Modifier
                        .padding(top = 16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // If the user is logged in, call the ifLogged function after a delay
                LaunchedEffect(Unit) {
                    delay(SplashWaitingTime)
                    ifLogged()
                }
            }
        }


    }

}