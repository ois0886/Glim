package com.ssafy.glim.feature.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.feature.home.HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions) {
    navigate(BottomTabRoute.Home, navOptions)
}

fun NavGraphBuilder.homeNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<BottomTabRoute.Home> {
        HomeRoute(
            padding = padding,
        )
    }
}
