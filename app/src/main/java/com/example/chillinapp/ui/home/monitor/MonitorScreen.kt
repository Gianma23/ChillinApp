package com.example.chillinapp.ui.home.monitor

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.navigation.NavigationDestination

object MonitorDestination: NavigationDestination {
    override val route: String = "overall"
    override val titleRes: Int = R.string.overview_title
}

@Composable
fun MonitorScreen(
    modifier: Modifier = Modifier,
    viewModel: MonitorViewModel = viewModel()
) {

    /*TODO: Implement the UI for the Overall screen.*/

    Text(text = stringResource(id = MonitorDestination.titleRes))

}