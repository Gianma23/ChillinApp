package com.example.chillinapp.ui.home.overall

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.AppViewModelProvider
import com.example.chillinapp.ui.navigation.NavigationDestination

object OverallDestination: NavigationDestination {
    override val route: String = "overall"
    override val titleRes: Int = R.string.overview_title
}

@Composable
fun OverallScreen(
    modifier: Modifier = Modifier,
    navigateToSettingsScreen: () -> Unit = {},
    navigateToMapScreen: () -> Unit = {},
    viewModel: OverallModelView = viewModel(factory = AppViewModelProvider.Factory)
) {

}