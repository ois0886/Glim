package com.ssafy.glim.feature.glimdetail.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ssafy.glim.core.navigation.GlimRoute
import com.ssafy.glim.feature.glimdetail.GlimListRoute
import com.ssafy.glim.feature.glimdetail.GlimListType


fun NavGraphBuilder.glimListNavGraph(
    popBackStack: () -> Unit,
    padding: PaddingValues
) {
    composable<GlimRoute.Liked> { backStackEntry ->
        GlimListRoute(
            padding = padding,
            popBackStack = popBackStack,
            listType = GlimListType.LIKED
        )
    }

    composable<GlimRoute.Upload> { backStackEntry ->
        GlimListRoute(
            padding = padding,
            popBackStack = popBackStack,
            listType = GlimListType.UPLOADED
        )
    }
}