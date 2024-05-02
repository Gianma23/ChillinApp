package com.example.chillinapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.chillinapp.ui.home.map.MapDestination
import com.example.chillinapp.ui.home.monitor.MonitorDestination
import com.example.chillinapp.ui.home.settings.SettingsDestination

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Monitor : Screen(MonitorDestination.route, "Monitor", Icons.Default.Home)
    data object Map : Screen(MapDestination.route, "Map", Icons.Default.Map)
    data object Settings : Screen(SettingsDestination.route, "Settings", Icons.Default.Settings)
}
