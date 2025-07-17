package com.example.myapplication.feature.reels.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.myapplication.core.navigation.BottomTabRoute
import com.example.myapplication.feature.reels.ReelsRoute

fun NavController.navigateToReels(navOptions: NavOptions) {
    navigate(BottomTabRoute.Reels, navOptions)
}

fun NavGraphBuilder.reelsNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<BottomTabRoute.Reels> {
        ReelsRoute(
            padding = padding,
            popBackStack = popBackStack,
        )
    }
}
