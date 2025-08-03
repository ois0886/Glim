package com.ssafy.glim.feature.myglims.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ssafy.glim.core.navigation.MyGlimsRoute
import com.ssafy.glim.feature.myglims.MyGlimsRoute
import com.ssafy.glim.feature.myglims.MyGlimsType

fun NavGraphBuilder.myGlimsNavGraph(
    popBackStack: () -> Unit,
    padding: PaddingValues,
) {
    composable<MyGlimsRoute.Liked> { backStackEntry ->
        MyGlimsRoute(
            padding = padding,
            popBackStack = popBackStack,
            listType = MyGlimsType.LIKED,
        )
    }

    composable<MyGlimsRoute.Upload> { backStackEntry ->
        MyGlimsRoute(
            padding = padding,
            popBackStack = popBackStack,
            listType = MyGlimsType.UPLOADED,
        )
    }
}
