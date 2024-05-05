package com.example.chillinapp.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.chillinapp.ui.home.map.MapDestination
import com.example.chillinapp.ui.home.monitor.MonitorDestination
import com.example.chillinapp.ui.home.settings.SettingsDestination

/**
 * Sealed class representing the different screens in the application.
 *
 * This sealed class defines three screens: Monitor, Map, and Settings. Each screen is represented as an object that
 * extends the Screen class and provides a route, a label, and an icon. The route is used for navigation, the label is
 * used for display purposes, and the icon is used to visually represent the screen.
 *
 * @property route The route used for navigation to this screen.
 * @property label The label used for display purposes.
 * @property icon The icon used to visually represent this screen.
 */
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {

    /**
     * Object representing the Monitor screen.
     */
    data object Monitor : Screen(MonitorDestination.route, "Monitor", Icons.Default.MonitorHeart)

    /**
     * Object representing the Map screen.
     */
    data object Map : Screen(MapDestination.route, "Map", Icons.Default.Map)

    /**
     * Object representing the Settings screen.
     */
    data object Settings : Screen(SettingsDestination.route, "Settings", Icons.Default.Settings)
}
