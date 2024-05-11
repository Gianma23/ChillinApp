package com.example.chillinapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chillinapp.R
import com.example.chillinapp.ui.navigation.HomeNavGraph
import com.example.chillinapp.ui.navigation.NavigationDestination
import com.example.chillinapp.ui.theme.ChillInAppTheme


object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.home_screen_title
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    val homeNavController = rememberNavController()

    Scaffold(
        topBar = {
            HomeTopBar(homeNavController)
        },
        bottomBar = {
            HomeBottomBar(
                navController = homeNavController,
                items = listOf(
                    Screen.Monitor,
                    Screen.Map
                )
            )
        },
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { innerPadding ->
        HomeNavGraph(
            homeNavController = homeNavController,
            mainNavController = navController,
            modifier = Modifier
                .padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(navController: NavHostController) {

    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.size(16.dp))
                Text(
                    text = stringResource(id = HomeDestination.titleRes),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        actions = {
            IconButton(onClick = { if(navController.currentDestination?.route != Screen.Settings.route) {
                navController.navigate(Screen.Settings.route)
            } }) {
                Icon(Screen.Settings.icon, contentDescription = Screen.Settings.label)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
private fun HomeBottomBar(
    navController: NavHostController,
    items: List<Screen>
) {
    BottomAppBar(
        contentColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    if(currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    ChillInAppTheme {
        HomeScreen(navController = rememberNavController())
    }
}