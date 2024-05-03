package com.example.chillinapp.ui.home.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.navigation.NavigationDestination
import com.example.chillinapp.ui.theme.ChillInAppTheme

object SettingsDestination : NavigationDestination {
    override val route: String = "settings"
    override val titleRes: Int = R.string.settings_title
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navigateToLogInScreen: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel(
//        factory = AppViewModelProvider.Factory
    )
) {

    /*TODO: Implement the Settings screen.*/
    Surface(
        modifier = modifier
            .fillMaxSize()
    ) {

        Text(text = stringResource(id = SettingsDestination.titleRes))
    }

}

@Preview
@Composable
fun SettingScreenPreview(){
    ChillInAppTheme {
        SettingsScreen()
    }
}