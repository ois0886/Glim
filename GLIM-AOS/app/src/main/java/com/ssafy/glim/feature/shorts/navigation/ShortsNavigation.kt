package com.ssafy.glim.feature.shorts.navigation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.feature.shorts.ShortsRoute

fun NavController.navigateToShorts(navOptions: NavOptions) {
    navigate(BottomTabRoute.Shorts(), navOptions)
}

fun NavGraphBuilder.shortsNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<BottomTabRoute.Shorts> { navBackStackEntry ->
        val quoteId = navBackStackEntry.arguments?.getLong("quoteId") ?: -1
        Log.d("ShortsNavigation", "Navigating to Shorts with quoteId: $quoteId")
        ShortsRoute(
            quoteId = quoteId,
            padding = padding,
            popBackStack = popBackStack,
        )
    }
}
