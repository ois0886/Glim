package com.ssafy.glim.feature.profile.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.feature.profile.ProfileRoute

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
