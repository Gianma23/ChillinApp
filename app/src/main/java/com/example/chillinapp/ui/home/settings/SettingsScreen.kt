package com.example.chillinapp.ui.home.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.navigation.NavigationDestination

object SettingsDestination : NavigationDestination {
    override val route: String = "settings"
    override val titleRes: Int = R.string.settings_title
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navigateToLogInScreen: () -> Unit = {},
    navigateToOverallScreen: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel()
) {

    /*TODO: Implement the Settings screen.*/

    Text(text = stringResource(id = SettingsDestination.titleRes))

}