package com.ssafy.glim.feature.bookdetail.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.feature.bookdetail.BookDetailScreen

fun NavController.navigateToBookDetail(navOptions: NavOptions) {
    navigate(BottomTabRoute.BookDetail, navOptions)
}

fun NavGraphBuilder.bookDetailNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<BottomTabRoute.BookDetail> {
        BookDetailScreen(
            padding = padding,
            popBackStack = popBackStack
        )
    }
}
