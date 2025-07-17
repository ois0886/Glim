package com.example.myapplication.feature.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.myapplication.core.navigation.BottomTabRoute
import com.example.myapplication.feature.home.HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions) {
    navigate(BottomTabRoute.Home, navOptions)
}

fun NavGraphBuilder.homeNavGraph(
    padding: PaddingValues,
) {
    composable<BottomTabRoute.Home> {
        HomeRoute(
            padding = padding,
        )
    }
}
