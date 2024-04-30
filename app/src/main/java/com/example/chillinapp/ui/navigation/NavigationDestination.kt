package com.example.chillinapp.ui.navigation

import androidx.annotation.StringRes


/**
 * Interface for defining a navigation destination in the application.
 *
 * This interface provides a contract for creating navigation destinations. Each destination must have a unique route
 * and a string resource ID for the title to be displayed for the screen.
 */
interface NavigationDestination {

    /**
     * Unique name to define the path for a composable.
     *
     * This property represents the unique route for a navigation destination. It is used by the navigation system
     * to navigate to this destination.
     */
    val route: String

    /**
     * String resource id that contains the title to be displayed for the screen.
     *
     * This property represents the string resource ID for the title of the navigation destination. This title is
     * displayed when the destination is the current screen.
     */
    @get:StringRes
    val titleRes: Int
}