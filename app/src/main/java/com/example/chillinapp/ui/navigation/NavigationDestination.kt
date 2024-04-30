package com.example.chillinapp.ui.navigation

import androidx.annotation.StringRes

interface NavigationDestination {
    /**
     * Unique name to define the path for a composable
     */
    val route: String

    /**
     * String resource id to that contains title to be displayed for the screen.
     */
    @get:StringRes
    val titleRes: Int
}