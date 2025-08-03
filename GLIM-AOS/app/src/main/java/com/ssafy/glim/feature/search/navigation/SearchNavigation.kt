package com.ssafy.glim.feature.search.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.feature.search.SearchRoute

fun NavController.navigateToSearch(navOptions: NavOptions) {
    navigate(BottomTabRoute.Search, navOptions)
}

fun NavGraphBuilder.searchNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<BottomTabRoute.Search> {
        SearchRoute(
            padding = padding,
            popBackStack = popBackStack,
        )
    }
}
