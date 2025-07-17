package com.example.myapplication.feature.profile.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.myapplication.core.navigation.BottomTabRoute
import com.example.myapplication.feature.profile.ProfileRoute

fun NavController.navigateToProfile(navOptions: NavOptions) {
    navigate(BottomTabRoute.Profile, navOptions)
}

fun NavGraphBuilder.profileNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<BottomTabRoute.Profile> {
        ProfileRoute(
            padding = padding,
            popBackStack = popBackStack,
        )
    }
}
