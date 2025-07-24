package com.ssafy.glim.feature.reels.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.quote.feature.reels.ReelsRoute

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
