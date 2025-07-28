package com.ssafy.glim.feature.glimlist.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ssafy.glim.core.navigation.MyGlimsRoute
import com.ssafy.glim.feature.glimlist.GlimListRoute
import com.ssafy.glim.feature.glimlist.GlimListType

fun NavGraphBuilder.glimListNavGraph(
    popBackStack: () -> Unit,
    padding: PaddingValues,
) {
    composable<MyGlimsRoute.Liked> { backStackEntry ->
        GlimListRoute(
            padding = padding,
            popBackStack = popBackStack,
            listType = GlimListType.LIKED,
        )
    }

    composable<MyGlimsRoute.Upload> { backStackEntry ->
        GlimListRoute(
            padding = padding,
            popBackStack = popBackStack,
            listType = GlimListType.UPLOADED,
        )
    }
}
