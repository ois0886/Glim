package com.example.myapplication.feature.library.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.myapplication.core.navigation.BottomTabRoute
import com.example.myapplication.feature.library.LibraryRoute

fun NavController.navigateToLibrary(navOptions: NavOptions) {
    navigate(BottomTabRoute.Library, navOptions)
}

fun NavGraphBuilder.libraryNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<BottomTabRoute.Library> {
        LibraryRoute(
            padding = padding,
            popBackStack = popBackStack,
        )
    }
}
