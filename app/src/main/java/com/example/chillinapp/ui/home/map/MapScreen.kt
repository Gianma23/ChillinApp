package com.example.chillinapp.ui.home.map

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.navigation.NavigationDestination

object MapDestination : NavigationDestination {
    override val route: String = "map"
    override val titleRes: Int = R.string.map_screen_title
}

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel()
) {

    /*TODO: Implement Map screen*/

    Text(text = stringResource(id = MapDestination.titleRes))
}