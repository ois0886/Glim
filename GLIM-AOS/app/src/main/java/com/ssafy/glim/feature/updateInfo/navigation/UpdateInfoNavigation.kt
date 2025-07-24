package com.ssafy.glim.feature.updateInfo.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.feature.updateInfo.UpdateProfileRoute

fun NavGraphBuilder.updateInfoNavGraph(
    popBackStack: () -> Unit,
    padding: PaddingValues,
) {
    composable<Route.UpdateInfo> { backStackEntry ->
        UpdateProfileRoute(
            padding = padding,
            popBackStack = popBackStack,
        )
    }
}
