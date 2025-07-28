package com.ssafy.glim.feature.update.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ssafy.glim.core.navigation.UpdateInfoRoute
import com.ssafy.glim.feature.update.UpdateRoute
import com.ssafy.glim.feature.update.UpdateType

fun NavGraphBuilder.updateNavGraph(
    popBackStack: () -> Unit,
    padding: PaddingValues,
) {
    composable<UpdateInfoRoute.Personal> { backStackEntry ->
        UpdateRoute(
            updateType = UpdateType.PERSONAL,
            padding = padding,
            popBackStack = popBackStack,
        )
    }

    composable<UpdateInfoRoute.Password> {
        UpdateRoute(
            updateType = UpdateType.PASSWORD,
            padding = padding,
            popBackStack = popBackStack,
        )
    }
}
