package com.ssafy.glim.feature.celebrations.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.feature.celebrations.CelebrationsRoute

fun NavGraphBuilder.celebrationsNavGraph(
    padding: PaddingValues,
) {
    composable<Route.Celebration> { stackEntry ->
        val route = stackEntry.toRoute<Route.Celebration>()

        CelebrationsRoute(
            padding = padding,
            nickname = route.nickname,
        )
    }
}
